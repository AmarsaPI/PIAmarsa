from django.db import models
from .empleado_model import Empleado

class Fichaje(models.Model):
    id = models.AutoField(primary_key=True)
    empleado = models.ForeignKey(Empleado, on_delete=models.CASCADE)
    in_datetime = models.DateTimeField(verbose_name="Entrada")
    out_datetime = models.DateTimeField(verbose_name="Salida", blank= True, null=True)
    notes = models.CharField(verbose_name="Notas", null=True)