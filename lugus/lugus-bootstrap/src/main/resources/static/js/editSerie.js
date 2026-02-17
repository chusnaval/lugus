document.addEventListener('DOMContentLoaded', () => {
	const form = document.getElementById('formCaratula');
	const caratulaImg = document.getElementById('caratulaImg');

	form.addEventListener('submit', async (e) => {
		e.preventDefault(); // Evita recarga

		const actionUrl = form.getAttribute('action');
		const formData = new FormData(form);

		try {
			const response = await fetch(actionUrl, {
				method: 'POST',
				body: formData
			});

			if (!response.ok) throw new Error('Error al enviar la carátula');
			const idSerie = document.getElementById('idSerie');
			// Refrescar la imagen, evitando la caché del navegador
			const nuevaSrc = '/lugus/series/' + idSerie.value + '/image';
			caratulaImg.src = nuevaSrc;

		} catch (err) {
			console.error(err);
			alert('Hubo un error al añadir la carátula.');
		}
	});


});
