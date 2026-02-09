document.getElementById("migrateButton").addEventListener("click", async function () {
    const value = document.getElementById("newLocation").value;

    if (!value) {
        alert("Selecciona una ubicación primero");
        return;
    }

    const url = `/locations/migrate/old/${oldCode}/new/${value}`;

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("Error en la migración");
        }

        // cerrar modal solo si fue bien
        const modal = bootstrap.Modal.getInstance(
            document.getElementById("confirmModal")
        );
        modal.hide();

        alert("Migración completada");

        // opcional: refrescar datos de la página sin reload
        // reloadTable();
    } catch (error) {
        alert("Falló la operación");
        console.error(error);
    }
});
