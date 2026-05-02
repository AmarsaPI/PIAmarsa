from django.shortcuts import render, redirect
from django.http import HttpResponse
from django.urls import reverse
from django.contrib.auth import authenticate, login, logout
from .forms.login_form import LoginForm
from empleados.views import dashboard_view

def login_view(request):
    if request.POST:
        formulario = LoginForm(request.POST)

        if formulario.is_valid():
            username = formulario.cleaned_data["username"]
            password = formulario.cleaned_data["password"]

            empleado = authenticate(request, username=username, password=password)

            if empleado is not None:
                login(request, empleado)
                return redirect(reverse("dashboard_view"))
            else:
                context = {
                    "formulario": formulario,
                    "errorMessage": "Credenciales incorrectas"
                }
                return render(request, "login.html", context)

    else:
        formulario = LoginForm()
        
        context = {
            "formulario": formulario
        }

        return render(request, "login.html", context)
    
def logout_view(request):
    logout(request)
    return redirect(reverse("login_view"))