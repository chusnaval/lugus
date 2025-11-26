const btnSave = document.getElementById('guardar');
const btnAceptar = document.getElementById('acceptBtn');
const btnCancelar = document.getElementById('cancelBtn');

const modal = new bootstrap.Modal(document.getElementById('confirmModal'));

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

const lfuentes = async function() {
	const res = await fetch('/lugus/fuentes', {
		method: 'GET',
		credentials: 'same-origin'
	});
	if (!res.ok) {
		throw new Error(`Error ${res.status}: ${res.statusText}`);
	}
	return res.json();
}

var valFuentes;
const ejecutarFuentes = async function() {
	valFuentes = await lfuentes();
}();

const fuenteSelect = document.getElementById("fuente");

function calculateFuente(event) {
	const valor = event.target.value;

	const coincidencia = valFuentes.find(fuente =>
		// Si el suggest está contenido en el texto del input
		valor.includes(fuente.suggest.toLowerCase())
	);

	if (coincidencia) {
		fuenteSelect.value = coincidencia.id;
	}
}
const urlInput = document.getElementById('url');
urlInput.addEventListener('input', calculateFuente);
