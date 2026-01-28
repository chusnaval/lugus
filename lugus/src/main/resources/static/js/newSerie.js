const btnSave = document.getElementById('guardar');
const btnAceptar = document.getElementById('acceptBtn');
const btnCancelar = document.getElementById('cancelBtn');

const modal = new bootstrap.Modal(document.getElementById('confirmModal'));

const formulario = document.getElementById('formulario');

btnSave.addEventListener('click', async (e) => {
	try {
		e.preventDefault();
		const result = await validateTitleAndYear();

		if (result.titulo == null) {
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
	const inputYear = document.getElementById("anyoInicio");

	return fetch('/lugus/validateSerie/titlesInYear?title=' + encodeURIComponent(inputTitle.value) + "&titleGest=" + encodeURIComponent(inputTitleGest.value) + "&year=" + encodeURIComponent(inputYear.value), {
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

const lsources = async function() {
	const res = await fetch('/lugus/v1/api/sources/suggested', {
		method: 'GET',
		credentials: 'same-origin'
	});
	if (!res.ok) {
		throw new Error(`Error ${res.status}: ${res.statusText}`);
	}
	return res.json();
}

var valSources;
const execSources = async function() {
	valSources = await lsources();
}();

const sourceSelect = document.getElementById("source");

function calculateSource(event) {
	const valor = event.target.value;

	const coincidencia = valSources.find(source =>
		// Si el suggest está contenido en el texto del input
		valor.includes(source.suggest.toLowerCase())
	);

	if (coincidencia) {
		sourceSelect.value = coincidencia.id;
	}
}
const urlInput = document.getElementById('url');
urlInput.addEventListener('input', calculateSource);
