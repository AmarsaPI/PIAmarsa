from django import forms
from empleados.models import empleado_model

class LoginForm(forms.Form):
    username = forms.CharField(label="", required=True, widget=forms.TextInput({"placeholder": "Email"}))
    password = forms.CharField(label="", widget=forms.PasswordInput({'placeholder': 'Contraseña'}))