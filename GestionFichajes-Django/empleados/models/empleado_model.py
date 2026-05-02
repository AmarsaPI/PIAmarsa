from django.db import models
from django.contrib.auth.base_user import AbstractBaseUser, BaseUserManager

class EmpleadoManager(BaseUserManager):
    def create_user(self, username, first_name, last_name, is_admin = False, password = None):
        empleado = self.model(
            username = self.normalize_email(username),
            first_name = first_name,
            last_name = last_name,
            is_admin = is_admin
        )

        empleado.set_password(password)
        empleado.save()
        return empleado
    
    def create_superuser(self, username, first_name, last_name, password):
        empleado = self.create_user(
            username = self.normalize_email(username),
            first_name = first_name,
            last_name = last_name,
            password=password,
            is_admin = True
        )

        empleado.save()
        return empleado


class Empleado(AbstractBaseUser):
    username = models.EmailField(verbose_name="Email", unique=True)
    dni = models.CharField(verbose_name="DNI", max_length=9, blank=True)
    first_name = models.CharField(verbose_name="Nombre", null=False, max_length=50)
    last_name = models.CharField(verbose_name="Apellido", null=False, max_length=70)
    address = models.CharField(verbose_name="Domicilio", max_length=200)
    obs = models.TextField(verbose_name="Observaciones", blank=True)
    is_active = models.BooleanField(verbose_name="Activo", default=True)
    is_admin = models.BooleanField(verbose_name="Administrador", default=False)
    objects = EmpleadoManager()

    USERNAME_FIELD = "username"
    REQUIRED_FIELDS = ["first_name", "last_name"]

    def __str__(self):
        return self.first_name + " " + self.last_name
    
    def has_perm(self, perm, obj = None):
        return True
    
    def has_module_perms(self, app_label):
        return True
    
    @property
    def is_staff(self):
        return self.is_admin