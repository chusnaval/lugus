const btnSave = document.getElementById('guardar');
const btnAceptar = document.getElementById('acceptBtn');
const btnCancelar = document.getElementById('cancelBtn');

const modal = new bootstrap.Modal(document.getElementById('confirmModal'));

const formulario = document.getElementById('formulario');
let isSubmitting = false;

const disableActionButtons = () => {
	btnSave.disabled = true;
	btnAceptar.disabled = true;
	btnCancelar.disabled = true;
};

const enableActionButtons = () => {
	btnSave.disabled = false;
	btnAceptar.disabled = false;
	btnCancelar.disabled = false;
};

const submitOnce = () => {
	if (isSubmitting) {
		return;
	}
	isSubmitting = true;
	disableActionButtons();
	formulario.submit();
};

btnSave.addEventListener('click', async (e) => {
	try {
		e.preventDefault();
		if (isSubmitting) {
			return;
		}
		btnSave.disabled = true;
		const result = await validateTitleAndYear();

		if (result.titulo == null) {
			submitOnce();
		} else {

			modal.show();
			btnAceptar.disabled = false;
			btnCancelar.disabled = false;

			const onAceptar = () => {
				submitOnce();
				modal.hide();
			};
			const onCancelar = () => {
				modal.hide();
				enableActionButtons();
			};

			btnAceptar.addEventListener('click', onAceptar, { once: true });
			btnCancelar.addEventListener('click', onCancelar, { once: true });
		}


	} catch (err) {
		console.error(err);
		enableActionButtons();
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
