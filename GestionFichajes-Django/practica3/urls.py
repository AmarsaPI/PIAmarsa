"""
URL configuration for practica3 project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/6.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from practica3.views import login_view, logout_view
from empleados.views import *
urlpatterns = [
    path('empleados/', empleados_view, name="empleados_view"),
    path('empleados/new/', empleados_new),
    path('empleados/edit/<int:id_emp>/', empleados_edit),
    path('empleados/delete/<int:id_emp>/', empleados_delete),
    path('empleados/list/<int:id_emp>/', fichajes_list_view),
    path('fichajes/', fichajes_view),
    path('cuenta/', cuenta_view),
    path('login/', login_view, name="login_view"),
    path('logout/', logout_view, name="logout_view"),
    path('dashboard/', dashboard_view, name="dashboard_view"),
    path('', dashboard_view, name="dashboard_view"),
    path('admin/', admin.site.urls)
]
