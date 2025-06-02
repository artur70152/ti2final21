let currentUser = '';

// Função para carregar o perfil do usuário
// let emaillogado='arturf123ss@gmail.com';
const formData = new FormData();

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
const email = urlParams.get('emaillogado');
const emailteste = emaillogado;  // Ajuste conforme a lógica

const email22 = email;
//const users = JSON.parse(email);
//console.log(users[0].email);


//let emaillogado=users[0].email;

const params = new URLSearchParams(window.location.search);

// Extrair os valores

const email2 = params.get("email");

//let emailteste=users[0].email;

//let emailteste=email2;

//let emaillogado='arturf123ss@gmail.com';
//let emailteste='arturf123ss@gmail.com';
function loadSendMessageButton() {
    const container = document.getElementById('sendMessageButtonContainer');
    if (emaillogado !== email) {
        const button = document.createElement('button');
        button.className = 'sendmessagebutton';
        button.textContent = 'mandar primeira mensagem';
       // mudarCorDeFundo();
        button.onclick = () => sendmessagebutton(emailteste);
        container.appendChild(button);
    }
}
function mudarCorDeFundo2(cor1, cor2) {
   // console.log("bbbb");
    // Obtém a cor atual do fundo no formato RGB
    const backgroundColor = getComputedStyle(document.body).backgroundColor;

    // Converter cores hexadecimais para RGB
    const hexToRgb = (hex) => {
        const bigint = parseInt(hex.slice(1), 16);
        const r = (bigint >> 16) & 255;
        const g = (bigint >> 8) & 255;
        const b = bigint & 255;
        return `rgb(${r}, ${g}, ${b})`;
    };

    // Converte as cores para RGB
    const cor1Rgb = hexToRgb(cor1);
    const cor2Rgb = hexToRgb(cor2);

    // Verifica a cor atual e alterna entre cor1 e cor2
    if (backgroundColor === cor1Rgb) {
        document.body.style.backgroundColor = cor2;
    } else if (backgroundColor === cor2Rgb) {
        document.body.style.backgroundColor = cor1;
    } else {
        // Define uma cor padrão caso o fundo não esteja configurado
        document.body.style.backgroundColor = cor1;
    }
}
function loadcollor() {
  //  const container1 = document.getElementById('color');
 
    //    const button1 = document.createElement('button');
     //   button1.className = 'sendmessagebutton2';
    //    button1.textContent = 'TEMA';
       // mudarCorDeFundo();
      // let coratual= '#f0f2f5';
       //let proximacor='#323538';
      // console.log("aaaa");
    //   button1.addEventListener('click', () => {
        //console.log('Botão clicado! Chamando mudarCorDeFundo.');
     //   mudarCorDeFundo2(coratual, proximacor);
 //   });
     
     //   container1.appendChild(button1);
   
}


// Chama a função após carregar a página
document.addEventListener('DOMContentLoaded', () => {
    loadSendMessageButton();
    loadcollor();
});

function mudarCorDeFundo() {
    document.body.style.backgroundColor = color;
}
console.log("email logado :"+emaillogado+" email visitado"+email);

function loadProfile(email) {

	fetch(`/get-user/${email}`)

.then(response => response.json())
.then(user => {
    const profileButton = document.getElementById('profileButton');
    const profileName = document.getElementById('profileName');

    // Atualiza a imagem e adiciona evento de clique
	console.log(user.foto);
    profileButton.innerHTML = `<img src='${user.foto}' alt="Foto do perfil" id="profileImage">`;
    profileName.textContent = user.name;

    const profileImage = document.getElementById('profileImage');
    if (emaillogado === email22) {
        profileImage.onclick = () => updateProfilePhoto(email);
    } else {
        profileImage.onclick = null; // Remove qualquer evento existente
    }
})
.catch(error => console.error('Erro ao carregar perfil:', error));
const nomeemail = document.getElementById('nomedeperfil');
nomeemail.innerText=email;

}
function loadUserToLocalStorage(email) {
fetch(`/get-user/${emailteste}`) // Substitua pelo email do usuário
.then(response => {
    if (!response.ok) {
        throw new Error(`Erro ao carregar dados: ${response.statusText}`);
    }
    return response.json();
})
.then(userData => {
    // Salva os dados do usuário no LocalStorage
    localStorage.setItem('currentUser', JSON.stringify(userData));
    console.log('Dados do usuário carregados para o LocalStorage:', userData);
})
.catch(error => console.error('Erro ao carregar dados para o LocalStorage:', error));
}

document.addEventListener('DOMContentLoaded', () => {
const userEmail = emailteste; // Substitua pelo email do usuário desejado
loadUserToLocalStorage(userEmail);
});

function updateLocalStorage(email) {
/*
fetch(`/get-user/${email}`)
.then(response => {
    if (!response.ok) {
        throw new Error(`Erro ao atualizar LocalStorage: ${response.statusText}`);
    }
    return response.json();
})
.then(userData => {
    localStorage.setItem('currentUser', JSON.stringify(userData));
    console.log('LocalStorage atualizado:', userData);
})
.catch(error => console.error('Erro ao sincronizar LocalStorage:', error));
*/

}


function updateProfilePhoto(email) {
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
   // 'arturf123ss@gmail.com'
    // Envia a nova foto para o backend
    fetch('/upload', {
        method: 'POST',
        body: formData,
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro ao fazer upload: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
			
            let novaFoto = data.filePath;
			console.log(novaFoto);
            // Atualiza o campo "foto" no JSON do usuário
            return fetch('/update-profile-photo', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, foto: novaFoto }),
            });
        })
		
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro ao atualizar foto de perfil: ${response.statusText}`);
            }
            return response.json();
        })
        .then(result => {
            alert(result.message || "Foto de perfil atualizada com sucesso!");
            loadProfile(email); // Recarrega o perfil para exibir a nova foto
        })
        .catch(error => console.error('Erro ao atualizar foto de perfil:', error));
}
};

input.click();
  // Abre o seletor de arquivos
}
function loadArtes(email,nome1) {
    fetch(`/get-all-artes`)
        .then(response => response.json())
        .then(artes => {
            const feed = document.getElementById('feed');
            feed.innerHTML = '';

            // ✅ Filtra apenas as artes do usuário		
				const minhasArtes = artes.filter(arte => arte.usuario === email);

            minhasArtes.reverse();

            minhasArtes.forEach((arte, index) => {
                const originalIndex = minhasArtes.length - 1 - index;

                const photoContainer = document.createElement('div');
                photoContainer.classList.add('photo-container');

                const img = document.createElement('img');
				let url = arte.arte;

							let basePath = "https://arturstorage123.blob.core.windows.net/upload/";
							let resultado = url.replaceAll(basePath, "");
							let nome=resultado;
							resultado=basePath+resultado;
              //  img.src = `${arte.arte}`;
			  
			  	let hyphenIndex = nome.indexOf('-');
			  			   let dotIndex = nome.indexOf('.');

			  			   if (hyphenIndex === -1 || dotIndex === -1 || dotIndex <= hyphenIndex) {
			  			       return ''; // Retorna vazio se não encontrar ou se estiver fora de ordem
			  			   }
			  let nn=nome.substring(hyphenIndex + 1, dotIndex);
			  
			  
				img.src = resultado;
				
                img.classList.add('photo');
                img.alt = "Arte do usuário";

                const actions = document.createElement('div');
                actions.classList.add('photo-actions');

                let result = extractBetweenHyphenAndDot(arte.arte);
                if (result.length === 0) {
                    result = arte.arte;
                }
                if (arte.curtidas === undefined) {
                    arte.curtidas = 0;
                }

                actions.innerHTML = `
<span class="photo-action" 				onclick="likePhoto(${originalIndex}, '${email}', '${email}', ${arte.id})">
👍 Curtir <span id="likes-count-${originalIndex}">${arte.curtidas}</span>
</span>

<span class="photo-action" onclick="toggleComments(${index})">💬 Comentar</span>

${emaillogado === emailteste ? 
`<span class="photo-action" onclick="removePhoto(${arte.id}, '${email}')">
💬 Remover</span>` : 
''}

<span class="aa" value='${arte.name}'>nome: ${nome1}</span>
<button class="aac" onclick="editarDescricao(${arte.id}, ${index}, '${resultado}')">IA</button>

`;

                const commentsSection = document.createElement('div');
                commentsSection.classList.add('comments-section');
                commentsSection.style.display = 'none';
                commentsSection.id = `comments-section-${index}`;

                // ✅ Mostra os comentários persistidos
                if (arte.comentarios && arte.comentarios.length > 0) {
                    arte.comentarios.forEach(comment => {
                        const commentDiv = document.createElement('div');
                        commentDiv.classList.add('comment');
                        commentDiv.innerHTML = `
                            <span class="comment-user">${comment.usuario}:</span>
                            <span class="comment-text">${comment.texto}</span>
                        `;
                        commentsSection.appendChild(commentDiv);
                    });
                }

                const newCommentDiv = document.createElement('div');
                newCommentDiv.classList.add('new-comment');
                newCommentDiv.innerHTML = `
                    <input type="text" id="new-comment-input-${index}" placeholder="Escreva um comentário...">
<button onclick="addComment(${index}, '${email}', ${arte.id})">Enviar</button>
                `;
                commentsSection.appendChild(newCommentDiv);

                photoContainer.appendChild(img);
			

				// ✅ Adiciona a descrição abaixo da imagem
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
    
    updateLocalStorage(email);
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


function doarpara(email) {
    alert("descricao da ia");
}
function toggleComments(index) {
    console.log("aaa"+index);
    const commentsSection = document.getElementById(`comments-section-${index}`);
    commentsSection.style.display = commentsSection.style.display === 'none' ? 'block' : 'none';
  //  updateLocalStorage(emaillogado); // ou outra variável que tenha o email!
}


function sendmessagebutton(email) {
console.log(emailteste+" "+email22);
if (emailteste==email22) {
    return;
}
  fetch('/add-message', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      usuario: emailteste,   // destinatário
      texto: '.',           // conteúdo da mensagem
      remetente: email22 // remetente
    })
  })
  .then(response => {
    if (!response.ok) throw new Error(`Erro: ${response.statusText}`);
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

function removePhoto(arteId, email) {
    if (!confirm("Você tem certeza de que deseja remover esta arte?")) return;

    fetch('/remove-arte', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            arteId: arteId
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
        loadArtes(email);  // ✅ Recarregar artes após remoção
    })
    .catch(error => console.error('Erro ao remover arte:', error));

   // updateLocalStorage(email);
}


function likePhoto(index, email, usuario, arteId) {
  console.log("Curtindo arte:", arteId, "Usuário:", emaillogado, " Dono da arte:", email);

  fetch('/update-curtidas', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: email,           // ✅ Dono da arte
      arteIndex: arteId,      // ✅ ID real da arte
      usuario: emaillogado    // ✅ Quem está curtindo (usuário logado)
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

    if (data.message === 'Curtido') {
      currentLikes += 1;
    } else if (data.message === 'Descurtido') {
      currentLikes = Math.max(currentLikes - 1, 0);
    }

    likesCount.textContent = currentLikes;
  })
  .catch(error => {
    alert(`Erro: ${error.message}`);
    console.error('Erro ao registrar curtida:', error);
  });

  updateLocalStorage(email);
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
    let email = email22;

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
				console.log(convo.usuario, email22, convo, convo.remetente);
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
          if (msg.remetente === email22) {
			
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

    const remetente = email22;
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
	  const destinatario2 = email22;  // ✅ corretamente definido no clique anterior

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

//index email originalIndex

function addComment(index, email, arteId) {
    const input = document.getElementById(`new-comment-input-${index}`);
    const commentText = input.value.trim();

    if (commentText === '') {
        alert("O comentário não pode estar vazio!");
        return;
    }

    // ✅ Já mostra o comentário visualmente
    const commentsSection = document.getElementById(`comments-section-${index}`);
    const commentDiv = document.createElement('div');
    commentDiv.classList.add('comment');
    commentDiv.innerHTML = `
        <span class="comment-user">${email22}:</span>
        <span class="comment-text">${commentText}</span>
    `;
    commentsSection.insertBefore(commentDiv, commentsSection.lastChild);

    input.value = '';

    // ✅ Envia ao backend com o ID real da arte
    fetch(`/add-comment`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            arteId: arteId,
            usuario: email22,
            texto: commentText
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Erro ao adicionar comentário.");
        }
        return response.json();
    })
    .then(data => {
        console.log("Comentário adicionado:", data);
        // ✅ Pode recarregar se quiser, mas já está na tela
        // loadArtes(email);
    })
    .catch(error => console.error('Erro ao adicionar comentário:', error));
}


function editProfile() {
	
    let email=emaillogado;
    if (emaillogado !== email22) {
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
	let nn=prompt("nomde da arte a ser adicionada ?");
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
                body: JSON.stringify({ email: email, arte: novaArte, name:nn,curtidas:0,usuariosQueCurtiram:[] }),
            })
                .then(response => response.json())
                .then(result => {
                    alert(result.message);
                    loadArtes(email, nn); // Recarrega as artes para mostrar a nova arte
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
window.location.href = `pp.html?email=${encodeURIComponent(email22)}`; // Substitua pelo caminho da sua página
}


loadProfile(emailteste);
loadArtes(emailteste);