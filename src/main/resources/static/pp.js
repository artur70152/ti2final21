let currentUser = '';

// Função para carregar o perfil do usuário
// let emaillogado='arturf123ss@gmail.com';


//let emaillogado='artur70152@gmail.com';
// let emailteste='artur70152@gmail.com';

//emeillogado=pessoa visitando perfil
//let emaillogado='arturf123ss@gmail.com';

//emailteste=perfil sendo visitado
//let emailteste='artur70152@gmail.com';

//let emaillogado='artur70152@gmail.com';
//let emailteste='arturf123ss@gmail.com';


const urlParams = new URLSearchParams(window.location.search);
const emaillogado = urlParams.get('email');
const emailteste = emaillogado;  // Ajuste conforme a lógica




//let emaillogado='arturf123ss@gmail.com';
//let emailteste='arturf123ss@gmail.com';

let temaEscuro = false; // estado global fora da função

function loadSendMessageButton() {
    const container = document.getElementById('sendMessageButtonContainer');
    
    const button = document.createElement('button');
    button.className = 'sendmessagebutton';
    button.textContent = 'Alterar Tema';

    // Cores iniciais
    let coratual = '#2563eb'; // Cor clara
    let proximacor = '#323538'; // Cor escura

    button.onclick = () => mudarCorDeFundo(coratual, proximacor);

   // container.appendChild(button);
}

function mudarCorDeFundo(cor1, cor2) {
    const nam = document.getElementById('profileName');

    if (temaEscuro) {
        document.body.style.background = cor1;
        nam.style.color = 'black';
    } else {
        document.body.style.background = cor2;
        nam.style.color = 'white';
    }

    temaEscuro = !temaEscuro;
}

// Chama ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    loadSendMessageButton();
});


function performSearch() {
	

    const query = document.getElementById('searchInput').value.trim();

    if (!query) {
        alert('Por favor, insira algo para pesquisar.');
        return;
    }

    document.getElementById('searchInput').value = "";

    fetch(`/get-user/${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Usuário não encontrado');
            }
            return response.json();
        })
        .then(user => {
            // Redireciona para o perfil se o usuário for encontrado
           	gotoemail(user.email);
        })
        .catch(error => {
            console.error('Erro na busca:', error);
            alert('Email não encontrado!');
        });
}





function loadProfile(email) {
//fetch(`/get-profile/${email}`)
fetch(`/get-user/${email}`)
.then(response => response.json())
.then(user => {
    const profileButton = document.getElementById('profileButton');
    const profileName = document.getElementById('profileName');

    // Atualiza a imagem e adiciona evento de clique
    profileButton.innerHTML = `<img src='${user.foto}' alt="Foto do perfil" id="profileImage">`;
    profileName.textContent = user.name;

    const profileImage = document.getElementById('profileImage');
    if (emaillogado === emailteste) {
        profileImage.onclick = () => updateProfilePhoto(email);
    } else {
        profileImage.onclick = null; // Remove qualquer evento existente
    }
})
.catch(error => console.error('Erro ao carregar perfil:', error));



}
function loadUserToLocalStorage(email) {

}

document.addEventListener('DOMContentLoaded', () => {
const userEmail = emailteste; // Substitua pelo email do usuário desejado
loadUserToLocalStorage(userEmail);
});

function updateLocalStorage(email) {

}


function updateProfilePhoto(emaillogado) {


    setTimeout(() => {
        //window.location.href = `index.html?email=${encodeURIComponent(email)}&background=${encodeURIComponent(backgroundColor)}`;
window.location.href = `index.html?email=${encodeURIComponent(emaillogado)}`;
}, 1000);
//updateLocalStorage(email);  // Abre o seletor de arquivos
}


function editarDescricao(arteId, index, urlimg) {
	console.log(urlimg);
	fetch("/descrever-arte", {
	   method: "POST",
	   headers: { "Content-Type": "application/json" },
	   body: JSON.stringify({ url: urlimg })
	})
	.then(res => res.json())
	.then(data => {
	   alert("Descrição sugerida: " + data.descricao);

	   // Usa a descrição da IA diretamente
	   fetch('/update-descricao', {
	       method: 'POST',
	       headers: {
	           'Content-Type': 'application/json'
	       },
	       body: JSON.stringify({
	           arteId: arteId,
	           descricao: data.descricao
	       })
	   })
	   .then(response => {
	       if (!response.ok) {
	           throw new Error("Erro ao atualizar descrição.");
	       }
	       return response.json();
	   })
	   .then(data2 => {
	       alert(data2.message);
	       const descricaoDiv = document.querySelectorAll('.arte-descricao')[index];
	       if (descricaoDiv) {
	           descricaoDiv.textContent = data.descricao;
	       }
	   })
	   .catch(error => {
	       console.error('Erro ao atualizar descrição:', error);
	       alert("Erro ao atualizar descrição.");
	   });

	})
	.catch(err => console.error("Erro ao chamar IA:", err));

}


function loadallArtes() {
	updateLocalStorage(emaillogado);
    fetch(`/get-all-artes`)
    .then(response => response.json())
    .then(artes => {
		artes.forEach((a, i) => {
		    console.log(`Arte ${i} - curtidas: `, a.curtidas);
		});
        const feed = document.getElementById('feed');
        feed.innerHTML = '';

        artes.reverse();
        emailstodos = artes;
        console.log("Artes carregadas:", emailstodos);

        artes.forEach((arte, index) => {
            if (!arte.arte || arte.arte.trim() === '') {
                return;
            }

            const photoContainer = document.createElement('div');
            photoContainer.classList.add('photo-container');

            let img = document.createElement('img');
			
			let url = arte.arte;

			let basePath = "https://arturstorage123.blob.core.windows.net/upload/";
			let resultado = url.replaceAll(basePath, "");
			let nome=arte.name;
			resultado=basePath+resultado;
			
			
		//	let hyphenIndex = nome.indexOf('-');
		//	   let dotIndex = nome.indexOf('.');
//
			//   if (hyphenIndex === -1 || dotIndex === -1 || dotIndex <= hyphenIndex) {
			//       return ''; // Retorna vazio se não encontrar ou se estiver fora de ordem
			//   }
let nn=arte.name;
			
			   
			console.log("Artes carregadas:", nn);
			
            img.src = resultado;
            img.classList.add('photo');
            img.alt = `Arte do usuário ${arte.usuario}`;

            const actions = document.createElement('div');
            actions.classList.add('photo-actions');
            
            let result = extractBetweenHyphenAndDot(arte.arte);
            if (result.length === 0) result = arte.arte;

            actions.innerHTML = `
           <span id="like-icon-${index}" class="photo-action" onclick="likePhoto(${index}, '${arte.usuario}', '${arte.usuario}', '${index}', '${arte.id}')">
  👍 Curtir <span id="likes-count-${index}">${arte.curtidas}</span>
</span>

                <span class="photo-action" onclick="toggleComments(${index})">💬 Comentar</span>
                <span class="aa" value='${arte.name}'>nome: ${nn}</span>
                <button class="aa" value="${arte.usuario}" onclick="gotoemail('${arte.usuario}')">${arte.usuario}</button>
				<button class="aac" onclick="editarDescricao(${arte.id}, ${index}, '${resultado}')">IA</button>

         
                `;

            const commentsSection = document.createElement('div');
            commentsSection.classList.add('comments-section');
            commentsSection.style.display = 'none';
            commentsSection.id = `comments-section-${index}`;

            arte.comentarios.forEach(comment => {
                const commentDiv = document.createElement('div');
                commentDiv.classList.add('comment');
                commentDiv.innerHTML = `
                    <span class="comment-user">${comment.usuario}:</span>
                    <span class="comment-text">${comment.texto}</span>
                `;
                commentsSection.appendChild(commentDiv);
            });

            const newCommentDiv = document.createElement('div');
            newCommentDiv.classList.add('new-comment');
            newCommentDiv.innerHTML = `
                <input type="text" id="new-comment-input-${index}" placeholder="Escreva um comentário...">
                <button onclick="addComment(${index}, '${arte.usuario}', '${index}', '${arte.id}')">Enviar</button>
            `;
            commentsSection.appendChild(newCommentDiv);

            photoContainer.appendChild(img);
			// Descrição
			const descricaoDiv = document.createElement('div');
			descricaoDiv.classList.add('arte-descricao');
			descricaoDiv.textContent = arte.descricao || "Sem descrição.";
			photoContainer.appendChild(descricaoDiv);
			img.onclick = () => {
			    descricaoDiv.style.display = (descricaoDiv.style.display === 'none') ? 'block' : 'none';
			};
            photoContainer.appendChild(actions);
            photoContainer.appendChild(commentsSection);
            feed.appendChild(photoContainer);
        });
    })
    .catch(error => console.error('Erro ao carregar artes:', error));
}
function gotoemail1(){
    setTimeout(() => {
    //window.location.href = `index.html?email=${encodeURIComponent(email)}&background=${encodeURIComponent(backgroundColor)}`;
    window.location.href = `index.html?email=${encodeURIComponent(emaillogado)}&emaillogado=${encodeURIComponent(emaillogado)}`;
}, 1000);
}

function gotoemail(email){
setTimeout(() => {
    //window.location.href = `index.html?email=${encodeURIComponent(email)}&background=${encodeURIComponent(backgroundColor)}`;
    window.location.href = `index.html?email=${encodeURIComponent(email)}&emaillogado=${encodeURIComponent(emaillogado)}`;
}, 1000);

}
function doarpara(email) {
  alert("descricao da ia");
}


function extractBetweenHyphenAndDot(inputString) {
// Encontra a posição do último '-' na string
const hyphenIndex = inputString.lastIndexOf('-');
// Encontra a posição do primeiro '.' após o '-'
const dotIndex = inputString.indexOf('.', hyphenIndex);

// Se ambos os índices forem válidos, extrai a substring
if (hyphenIndex !== -1 && dotIndex !== -1) {
return inputString.substring(hyphenIndex + 1, dotIndex);
}

// Retorna uma string vazia se não encontrar o padrão esperado
return '';
}
function toggleComments(index) {
    console.log("aaa"+index);
    const commentsSection = document.getElementById(`comments-section-${index}`);
    commentsSection.style.display = commentsSection.style.display === 'none' ? 'block' : 'none';
   // updateLocalStorage(emaillogado); // ou outra variável que tenha o email!
}

function sendmessagebutton(email) {
// Obtenha o usuário atual do localStorage
//const currentUserData = JSON.parse(localStorage.getItem('currentUser'));

// if (!currentUserData) {
//  alert("Erro: Usuário atual não encontrado no localStorage.");
//  return;
// }
if (emaillogado === emailteste) {
       return;
    } 
const currentUserEmail = emaillogado;

fetch('/add-message', {
method: 'POST',
headers: {
    'Content-Type': 'application/json'
},
body: JSON.stringify({
    usuario: emailteste, // Email do destinatário
    texto: '.', // Conteúdo da mensagem
    remetente: emaillogado // Email do remetente
})
})
.then(response => {
    if (!response.ok) {
        throw new Error(`Erro ao enviar mensagem: ${response.statusText}`);
    }
    return response.json();
})
.then(data => {
    alert("Mensagem enviada com sucesso!");
    console.log("Resposta do servidor:", data);
})
.catch(error => {
    console.error("Erro ao enviar mensagem:", error);
    alert("Erro ao enviar mensagem.");
});
}
function removePhoto(index, email) {
if (!confirm("Você tem certeza de que deseja remover esta arte?")) return;

fetch('/remove-arte', {
method: 'POST',
headers: { 'Content-Type': 'application/json' },
body: JSON.stringify({
    email: email,
    arteIndex: index,
}),
})
.then(response => {
    if (!response.ok) {
        throw new Error(`Erro ao remover arte: ${response.statusText}`);
    }
    return response.json();
})
.then(data => {
    alert(data.message || "Arte removida com sucesso!");
    loadArtes(email); // Recarregar as artes após a remoção
})
.catch(error => console.error('Erro ao remover arte:', error));
updateLocalStorage(email); 
}

function likePhoto(index, email, usuarioDono, index2, arteId) {
  console.log("Curtindo arte:", arteId, "Usuário:", emaillogado," aas ",email);  // ← loga quem está clicando!

  fetch('/update-curtidas', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: email,           // Dono da arte
      arteIndex: arteId,
      usuario: emaillogado    // ✅ Quem está clicando!!
    }),
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`Erro ao registrar curtida: ${response.statusText}`);
    }
    return response.json();
  })
  .then(data => {
    const likesCount = document.getElementById(`likes-count-${index}`);
    let currentLikes = parseInt(likesCount.textContent, 10);

    if (isNaN(currentLikes)) {
      currentLikes = 0;
    }

    // ✅ Ajuste: atualiza conforme mensagem
    if (data.message === 'Curtido') {
      currentLikes += 1;
    } else if (data.message === 'Descurtido') {
      currentLikes = Math.max(currentLikes - 1, 0);
    }

    likesCount.textContent = currentLikes;

    // ✅ Não recarrega mais:
    // setTimeout(() => { location.reload(); }, 500);
  })
  .catch(error => {
    alert(`Erro: ${error.message}`);
    console.error('Erro ao registrar curtida:', error);
  });
}








function toggleMessageList() {
  //  if (emaillogado !== emailteste) {
   //    return;
  //  } 
    const messageList = document.getElementById("messageList");
    messageList.style.display = messageList.style.display === "none" ? "block" : "none";
    loadConversations();
  //  updateLocalStorage(email); 
}

function loadConversations() {
    let email = emaillogado;

    fetch(`/get-messages/${email}`)
        .then(response => response.json())
        .then(conversations => {
            if (!Array.isArray(conversations)) {
                console.error('Resposta inesperada:', conversations);
                return;
            }

            const conversationList = document.getElementById('conversationList');
            conversationList.innerHTML = '';

            conversations.forEach(convo => {
				console.log(convo.usuario, emaillogado, convo, convo.remetente);
				if(emaillogado!=convo.remetente){
					console.log(convo.usuario, "asdasdasd", convo.remetente);
				}
                const convoDiv = document.createElement('div');
                convoDiv.classList.add('message-user');

          

      convoDiv.innerHTML = `
                    <img src="${convo.foto}" alt="Foto de ${convo.nome}" class="conversation-photo">
                    <span>${convo.nome || "Usuário Anônimo"}</span>
                `;

              

                convoDiv.onclick = () => {
					const mensagens = JSON.parse(convo.mensagens);
				console.log(mensagens[0].remetente);
				currentUser=mensagens[0].remetente;
					loadMessages(mensagens);

                };

                conversationList.appendChild(convoDiv);
            });
        })
        .catch(error => console.error('Erro ao carregar conversas:', error));
}



function loadMessages(messages) {
  
   console.log(messages);
   currentUser=messages[0].remetente;
   console.log(currentUser);
    const messagesDiv = document.getElementById('messages');
    messagesDiv.innerHTML = '';

    messages.forEach(msg => {
          if (msg.remetente === emaillogado) {
			
      return;
   } 
        const msgDiv = document.createElement('div');
        msgDiv.classList.add('message');
        msgDiv.textContent = msg.texto;
        messagesDiv.appendChild(msgDiv);
    });
 //  updateLocalStorage(email); 
}


function sendMessage() {
    const input = document.getElementById("newMessageInput");
    const messageText = input.value.trim();

    const remetente = emaillogado;
    const destinatario = currentUser;  // ✅ corretamente definido no clique anterior

    if (!messageText || !remetente || !destinatario) {
        alert("Erro: Texto da mensagem, remetente ou destinatário estão ausentes.");
        return;
    }

    fetch('/add-message', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            usuario: destinatario, // ✅ para quem a mensagem vai
            texto: messageText,
            remetente: remetente
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Erro ao enviar mensagem: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        console.log("Mensagem enviada:", data);
        input.value = '';

        const messagesDiv = document.getElementById('messages');
        const msgDiv = document.createElement('div');
        msgDiv.classList.add('message');
        msgDiv.textContent = `Você: ${messageText}`;
        messagesDiv.appendChild(msgDiv);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    })
    .catch(error => console.error('Erro ao enviar mensagem:', error));
	
	
	

	 const remetente2 = currentUser;
	  const destinatario2 = emaillogado;  // ✅ corretamente definido no clique anterior

	 if (!messageText || !remetente2 || !destinatario2) {
	     alert("Erro: Texto da mensagem, remetente ou destinatário estão ausentes.");
	     return;
	 }

	 fetch('/add-message', {
	     method: 'POST',
	     headers: { 'Content-Type': 'application/json' },
	     body: JSON.stringify({
	         usuario: destinatario2, // ✅ para quem a mensagem vai
	         texto: "Você: "+ messageText,
	         remetente: remetente2
	     })
	 })
	 .then(response => {
	     if (!response.ok) {
	         throw new Error(`Erro ao enviar mensagem: ${response.statusText}`);
	     }
	     return response.json();
	 })

	 .catch(error => console.error('Erro ao enviar mensagem:', error));

	
	
	
	
	
	
	
	
	
	
}



function addComment(index, email, originalIndex, arteId) {
    const input = document.getElementById(`new-comment-input-${index}`);
    const commentText = input.value.trim();

    if (commentText === '') {
        alert("O comentário não pode estar vazio!");
        return;
    }

    const commentsSection = document.getElementById(`comments-section-${index}`);
    const commentDiv = document.createElement('div');
    commentDiv.classList.add('comment');
    commentDiv.innerHTML = `
        <span class="comment-user">${emaillogado}:</span>
        <span class="comment-text">${commentText}</span>
    `;
    commentsSection.insertBefore(commentDiv, commentsSection.lastChild);

    input.value = '';

    fetch(`/add-comment`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            arteId: arteId,
            usuario: emaillogado,
            texto: commentText
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Erro ao adicionar comentário.");
        }
        return response.json();
    })
    .then(data => console.log("Comentário adicionado:", data))
    .catch(error => console.error('Erro ao adicionar comentário:', error));
}


function editProfile() {
    let email=emailteste;
    if (emaillogado !== emailteste) {
       return;
    } 
const input = document.createElement('input');
input.type = 'file';
input.accept = 'image/*';

input.onchange = function () {
const file = input.files[0];
if (file) {
    const formData = new FormData();
    formData.append('file', file);

    // Opcional: Salvar no servidor local ou usar Base64 diretamente
    fetch('/upload', {
        method: 'POST',
        body: formData,
    })
        .then(response => response.json())
        .then(data => {
            // Enviar o caminho da nova arte para o backend
            const novaArte = data.filePath; // URL ou caminho do arquivo retornado
            fetch('/add-arte', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, arte: novaArte }),
            })
                .then(response => response.json())
                .then(result => {
                    alert(result.message);
                    loadArtes(email); // Recarrega as artes para mostrar a nova arte
                })
                .catch(error => console.error('Erro ao adicionar arte:', error));
        })
        .catch(error => console.error('Erro ao fazer upload da imagem:', error));
}
};

input.click();
//updateLocalStorage(email); 
}

function openSettings() {
window.location.href = 'login.html';
localStorage.clear();
// Substitua pelo caminho da sua página
}


loadProfile(emailteste);
loadallArtes();