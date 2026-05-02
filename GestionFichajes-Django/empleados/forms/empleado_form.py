from django import forms
from empleados.models import empleado_model

class NewEmpleadoForm(forms.ModelForm):
    class Meta:
        model = empleado_model.Empleado
        fields = ["dni", "first_name", "last_name", "username", "address", "is_active", "is_admin", "obs"]
        
    # id = forms.CharField(label="DNI", max_length=9, required=True)
    # first_name = forms.CharField(label="Nombre", max_length=50, required=True)
    # last_name = forms.CharField(label="Apellido", required=True, max_length=70)
    # address = forms.CharField(label="Domicilio", max_length=200)
    # obs = forms.CharField(label="Observaciones", widget=forms.Textarea)