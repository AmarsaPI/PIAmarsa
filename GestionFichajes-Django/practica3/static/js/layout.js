"use strict";

window.onload = function() {
    let metaDescription = document.getElementsByTagName("meta")[0].content;
    let itemSelected = document.querySelector("#" + metaDescription);
    itemSelected.classList = "selected_item";
}

function acceptDialog(message = "", redirect = "") {
    const acceptDialog = document.getElementById("acceptDialog");
    let messageAccept = document.getElementById("messageAccept");
    messageAccept.innerText = message;
    acceptDialog.showModal();

    const closeDialog = document.getElementById("closeDialog");
    closeDialog.addEventListener("click", function() {
        acceptDialog.close();

        if (redirect != "") {
            window.location.href = redirect;
        }
    });
}

function yesNoDialog(message = "", id) {
    const yesNoDialog = document.getElementById("yesNoDialog");
    let messageYesNo = document.getElementById("messageYesNo");
    messageYesNo.innerText = message;
    yesNoDialog.showModal();

    const csrftoken = document.querySelector('[name=csrfmiddlewaretoken]').value;
    const yes = document.getElementById("yes");
    yes.addEventListener("click", function() {
        fetch(`/empleados/delete/${id}/`, {
            method: "DELETE",
            headers: { 
                'X-CSRFToken': csrftoken
            }
        }).then(response => {
            if (response.ok) {
                acceptDialog("El empleado ha sido eliminado");
                document.getElementById("delete_" + id).parentNode.parentElement.remove();
                yesNoDialog.close();
            }
        })
    });

    const no = document.getElementById("no");
    no.addEventListener("click", function() {
        yesNoDialog.close();
    });
}

function list(element, id) {
    window.location.href = "/empleados/list/" + id + "/";
}

function edit(element, id) {
    window.location.href = "/empleados/edit/" + id + "/";
}

function delete_emp(element, id) {
    yesNoDialog("Desea eliminar el empleado?", id);
}

function validationPass(event) {
    const pass = document.getElementById("pass");
    const passRepeat = document.getElementById("passRepeat");

    if (pass.value != passRepeat.value) {
        event.preventDefault();
        acceptDialog("Contraseñas no coinciden");
    }
}