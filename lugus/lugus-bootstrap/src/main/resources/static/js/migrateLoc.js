document.getElementById("migrateButton").addEventListener("click", async function () {
    const value = document.getElementById("newLocation").value;
	const oldCode = document.getElementById("idLocation").value;
    if (!value) {
        alert("Selecciona una ubicación primero");
        return;
    }

    const url = encodeURI(`/lugus/locations/migrate?oldLoc=${oldCode}&newLoc=${value}`);

    try {
        const response = await fetch(url, {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error("Error en la migración");
        }

        alert("Migración completada");

        // opcional: refrescar datos de la página sin reload
        // reloadTable();
    } catch (error) {
        alert("Falló la operación");
        console.error(error);
    }
});
