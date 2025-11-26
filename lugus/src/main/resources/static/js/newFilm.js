const btnSave = document.getElementById('guardar');
const btnAceptar = document.getElementById('acceptBtn');
const btnCancelar = document.getElementById('cancelBtn');

const modal =  new bootstrap.Modal(document.getElementById('confirmModal'));

btnSave.addEventListener('click', async (e) => {
	try {
		e.preventDefault();
		const result = await validateTitleAndYear();

		if (result.titulo==null) {
			formulario.submit();
		} else {

			modal.show();

			const onAceptar = () => {
				formulario.submit();
				modal.hide();
			};
			const onCancelar = () => {
				modal.hide();
			};

			btnAceptar.addEventListener('click', onAceptar, { once: true });
			btnCancelar.addEventListener('click', onCancelar, { once: true });
		}


	} catch (err) {
		console.error(err);
		alert('Error al comprobar la condición: ' + err.message);
	}
});

const validateTitleAndYear = async function() {
	const inputTitle = document.getElementById("titulo");
	const inputTitleGest = document.getElementById("tituloGest");
	const inputYear = document.getElementById("anyo");

	return fetch('/lugus/validate/titlesInYear?title=' + encodeURIComponent(inputTitle.value) + "&titleGest=" + encodeURIComponent(inputTitleGest.value) + "&year=" + encodeURIComponent(inputYear.value), {
		method: 'GET',
		credentials: 'same-origin'
	}).then(res => {
		if (!res.ok) {
			throw new Error(`Error ${res.status}: ${res.statusText}`);
		}
		return res.json();
	}).catch(err => {
		console.error('AJAX error:', err);
		throw err;
	});

}