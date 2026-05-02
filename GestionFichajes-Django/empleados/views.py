from django.shortcuts import render
from django.conf import settings
from django.contrib.auth.decorators import login_required
from django.contrib.admin.views.decorators import staff_member_required
from django.db import IntegrityError, Error
from django.views.decorators.http import require_http_methods
from django.http import JsonResponse
from .forms import NewEmpleadoForm
from .models import *
import datetime, time

# Create your views here.
@login_required
def dashboard_view(request):
    ultima_semana = datetime.datetime.now() - datetime.timedelta(days=7)
    fichajes_ultima_semana = Fichaje.objects.filter(
        empleado=request.user.id, 
        in_datetime__gte=ultima_semana,
        out_datetime__lte=datetime.datetime.now()
    ).order_by("-in_datetime")
    
    for fichaje in fichajes_ultima_semana:
        fichaje.total_time = format_timedelta((fichaje.out_datetime - fichaje.in_datetime).seconds)

    horas_totales_semana = format_timedelta(sum([
        (fichaje.out_datetime - fichaje.in_datetime).seconds 
        for fichaje in fichajes_ultima_semana
    ]))

    ultimo_mes = datetime.datetime.now() - datetime.timedelta(days=30)
    fichajes_ultimo_mes = Fichaje.objects.filter(
        empleado=request.user.id, 
        in_datetime__gte=ultimo_mes,
        out_datetime__lte=datetime.datetime.now()
    )

    horas_totales_mes = format_timedelta(sum([
        (fichaje.out_datetime - fichaje.in_datetime).seconds 
        for fichaje in fichajes_ultimo_mes
    ]))
    
    context = {
        "fichajes_ultima_semana": fichajes_ultima_semana,
        "horas_totales_semana": horas_totales_semana,
        "horas_totales_mes": horas_totales_mes
    }

    return render(request, "dashboard.html", context)

def format_timedelta(total_seconds):
    if not total_seconds:
        return "0:00"
    hours = total_seconds // 3600
    minutes = (total_seconds % 3600) // 60
    return f"{hours}h {minutes:02d}m"

@login_required
@staff_member_required(login_url=settings.LOGIN_URL)
def empleados_view(request):    
    list_empleados = Empleado.objects.all()
    
    context = {
        "list_empleados": list_empleados
    }

    return render(request, "empleados.html", context)

@login_required
@staff_member_required(login_url=settings.LOGIN_URL)
def empleados_new(request):
    formulario = NewEmpleadoForm()
    creado = False
    error = False
    
    if request.POST:
        new_empleado = Empleado()
        formulario = NewEmpleadoForm(request.POST, instance=new_empleado)
        
        if formulario.is_valid():
            try:
                if (request.POST["password"] != ""):
                    new_empleado.set_password(request.POST["password"])
                new_empleado.save(force_insert=True)
                creado = True
                formulario = NewEmpleadoForm()
            except IntegrityError:
                error = True

    context = {
        "formulario": formulario,
        "creado": creado,
        "error": error
    }

    return render(request, "new.html", context)

@login_required
@staff_member_required(login_url=settings.LOGIN_URL)
def empleados_edit(request, id_emp):
    error = False
    empleado_buscado = Empleado.objects.get(id=id_emp)

    formulario = NewEmpleadoForm(instance=empleado_buscado)
    context = { "formulario": formulario }

    if request.POST:
        formulario = NewEmpleadoForm(request.POST, instance=empleado_buscado)

        if formulario.is_valid():
            try:
                if (request.POST["password"] != ""):
                    empleado_buscado.set_password(request.POST["password"])
                empleado_buscado.save()
        
                context = {
                    "formulario": formulario,
                    "modificado": True
                }

                return render(request, "edit.html", context)
            except IntegrityError:
                error = True

    context = {
        "formulario": formulario,
        "error": error
    }

    return render(request, "edit.html", context)

@login_required
@require_http_methods(["DELETE"])
@staff_member_required(login_url=settings.LOGIN_URL)
def empleados_delete(request, id_emp):
    empleado_buscado = Empleado.objects.get(id=id_emp)
    errorDelete = False

    try:
        empleado_buscado.delete()
        return JsonResponse({"status": "ok"}, status=200)
    except Error:
        return JsonResponse({"error": "No se ha podido eliminar"}, status=404)

@login_required
def fichajes_view(request):
    ultimo_fichaje = Fichaje.objects.filter(
        empleado=request.user
    ).last()

    if request.POST:
        if ultimo_fichaje is None or ultimo_fichaje.out_datetime is not None:
            new_fichaje = Fichaje.objects.create(
                in_datetime=datetime.datetime.now(),
                empleado=request.user,
                notes=request.POST["note"]
            )
        else:
            ultimo_fichaje.out_datetime = datetime.datetime.now()
            note = f'{ultimo_fichaje.notes}. {request.POST["note"]}' if ultimo_fichaje.notes != "" else request.POST["note"]
            ultimo_fichaje.notes = note
            ultimo_fichaje.save()
    
    fichajes = Fichaje.objects.filter(
        empleado=request.user
    ).order_by("-id")

    for fichaje in fichajes:
        fichaje.total = format_timedelta((fichaje.out_datetime - fichaje.in_datetime).seconds) if fichaje.out_datetime is not None else "..."

    context = {
        "fichajes": fichajes
    }
    return render(request, 'fichajes.html', context)

@login_required
@staff_member_required(login_url=settings.LOGIN_URL)
def fichajes_list_view(request, id_emp):
    fichajes = Fichaje.objects.filter(
        empleado=id_emp
    ).order_by("-id")

    for fichaje in fichajes:
        fichaje.total = format_timedelta((fichaje.out_datetime - fichaje.in_datetime).seconds) if fichaje.out_datetime is not None else "..."

    context = {
        "fichajes": fichajes
    }
    return render(request, 'lista_fichajes.html', context)

@login_required
def cuenta_view(request):
    error = False
    empleado_buscado = Empleado.objects.get(id=request.user.id)

    formulario = NewEmpleadoForm(instance=empleado_buscado)
    if not request.user.is_admin:
        formulario.fields["is_admin"].disabled = True
    context = { "formulario": formulario }

    if request.POST:
        formulario = NewEmpleadoForm(request.POST, instance=empleado_buscado)

        if formulario.is_valid():
            try:
                if (request.POST["password"] != ""):
                    empleado_buscado.set_password(request.POST["password"])
                
                empleado_buscado.save()
        
                context = {
                    "formulario": formulario,
                    "modificado": True
                }

                return render(request, "cuenta.html", context)
            except IntegrityError:
                error = True

    context = {
        "formulario": formulario,
        "error": error
    }

    return render(request, "cuenta.html", context)