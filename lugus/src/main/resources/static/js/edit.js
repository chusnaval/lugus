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
			const idPelicula = document.getElementById('idPelicula');
			// Refrescar la imagen, evitando la caché del navegador
			const nuevaSrc = '/lugus/peliculas/' + idPelicula.value + '/image';
			caratulaImg.src = nuevaSrc;

		} catch (err) {
			console.error(err);
			alert('Hubo un error al añadir la carátula.');
		}
	});


});

const titulo = document.getElementById('campoTitulo').value;
const JSON_URL_DIR = '/lugus/directors/find/film/' + encodeURI(titulo);

const openBtn = document.getElementById('openModalBtnDir');
const modalContentDiv = document.getElementById('modalContent');
const tablaDirectores = document.getElementById('tablaDirectores');
const tablaInterpretes = document.getElementById('tablaInterpretes');

async function loadAndRender() {
	try {
		const resp = await fetch(JSON_URL_DIR, {
			headers: { 'Accept': 'application/json' },
			credentials: 'same-origin'
		});
		if (!resp.ok) throw new Error(`HTTP ${resp.status}`);

		const persons = await resp.json();   // array de objetos
		// Construimos la tabla manualmente (puedes usar template literals)
		const tableHtml = buildTable(persons);
		modalContentDiv.innerHTML = tableHtml;
	} catch (e) {
		console.error(e);
		modalContentDiv.innerHTML = `<div class="alert alert-danger m-3">
   Error al cargar datos JSON.
        </div>`;
	}
}

function borrar(id) {

}

document.querySelector('#tablaDirectores tbody')
	.addEventListener('click', e => {
		const target = e.target;
		if (target.matches('i.fa-delete-left')) {
			const personaId = target.id.substr(4);
			// Marcar hidden “nuevo_X” a true
			const hiddenNuevo = document.getElementById(`nuevo_${personaId}`);
			const hiddenBorrado = document.getElementById(`borrado_${personaId}`);
			if (hiddenNuevo.value === "true") {
				target.closest('tr').remove();
			} else {
				if(hiddenBorrado.value == "true"){
					hiddenBorrado.value = false;					
					document.getElementById(`nombre_${personaId}`).classList.remove('tachado');
				}else{
					hiddenBorrado.value = true;
					document.getElementById(`nombre_${personaId}`).classList.add('tachado');	
				}
				
			}

			console.log(`Persona ${personaId} eliminada (delegada)`);
		}
	});
// Helper que genera la tabla a partir del array JSON
function buildTable(list) {
	const rows = list.map(p => `
        <tr>
            <td class="col-nconst"><a onclick="descargar('${p.nconst}', '${p.primaryName}')">${p.nconst}</a></td>
       <td>${p.primaryName}</td>
            <td>${p.birthYear ?? '-'}</td>
            <td>${p.deathYear ?? '-'}</td>
            <td>${p.title ?? ''}</td>
        </tr>`).join('');

	return `
        <table class="table table-sm table-hover mb-0">
            <thead class="table-dark">
                <tr>
                    <th class="col-nconst">nconst</th>
                    <th>Nombre</th>
                    <th>Año nac.</th>
                    <th>Año fal.</th>
                    <th>Título</th>
                </tr>
            </thead>
            <tbody>
                ${rows}
            </tbody>
        </table>`;
}

function descargar(nconst, name) {
	const tbody = document.querySelector('#tablaDirectores tbody');
	tbody.appendChild(crearFila([nconst, true, name]));
}

function crearFila(vals) {
	const tr = document.createElement('tr');
	const td1 = document.createElement('td');
	const inputKey = document.createElement('input'); // 		<input type="hidden" id="key_${d.persona}" th:value="${d.persona}" /> 
	inputKey.type = 'hidden';
	inputKey.id = `key_` + vals[0];
	inputKey.value = `key_` + vals[0];

	const inputKey2 = document.createElement('input'); // 		<input type="hidden" id="nuevo_${d.persona}" th:value="false" />
	inputKey2.type = 'hidden';
	inputKey2.id = `nuevo_` + vals[0];
	inputKey2.value = vals[1];

	const spanNombre = document.createElement('span'); //  <span id="nombre_${d.persona}" th:text="${d.nombre}"></span>
	spanNombre.id = `nombre_` + vals[0];
	spanNombre.textContent = vals[2];

	td1.appendChild(inputKey);
	td1.appendChild(inputKey2);
	td1.appendChild(spanNombre);

	const td2 = document.createElement('td');
	const iBorrar = document.createElement('i'); // <i class="fa-solid fa-delete-left btn btn-sm btn-danger"></i>
	iBorrar.classList.add('fa-solid', 'fa-delete-left',
		'btn', 'btn-sm', 'btn-danger');
	iBorrar.id = `del_` + vals[0];
	td2.appendChild(iBorrar);

	tr.appendChild(td1);
	tr.appendChild(td2);
	return tr;
}

const personModal = document.getElementById('personModal');

personModal.addEventListener('show.bs.modal', () => {
	loadAndRender();
});

// si el usuario cierra el modal y lo vuelve a abrir,
// puedes limpiar el contenido para que el spinner vuelva a aparecer.
personModal.addEventListener('hide.bs.modal', () => {
	modalContentDiv.innerHTML = `
        <div class="d-flex justify-content-center align-items-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
            </div>
        </div>`;
});


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
