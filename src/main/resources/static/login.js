


async function login() {

    localStorage.clear();



    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;
    const mensagem = document.getElementById("mensagem");

    // Limpar mensagem anterior
    mensagem.textContent = "";

    // Validar campos vazios
    if (!email || !senha) {
        mensagem.textContent = "Por favor, preencha todos os campos.";
        return;
    }

    try {
        // Buscar dados do arquivo JSON
        const response = await fetch(`/get-user/${email}`);
        if (!response.ok) {
            throw new Error("Erro ao carregar o arquivo JSON.");
        }
        const usuarios = await response.json();
//console.log(usuarios);
        // Procurar usuário correspondente
        let usuarioEncontrado=false;
  if (usuarios.senha===senha) {
    usuarioEncontrado=true;
  }

        if (usuarioEncontrado) {
          //  console.log(usuarios);
            mensagem.textContent = `Bem-vindo, ${usuarios.name}!`;
            mensagem.className = "success";
        
        
            setTimeout(() => {
                window.location.href = `pp.html?email=${encodeURIComponent(email)}`; // Substitua "dashboard.html" pelo caminho desejado
            }, 2000); // 2000ms = 2 segundos
           // saveUserToLocal(usuarios);


            // Exemplo: Redirecionar para outra página
            // window.location.href = "dashboard.html";
        } else {
            mensagem.textContent = "E-mail ou senha incorretos.";
            mensagem.className = "error";
            
        }
    } catch (error) {
        console.error("Erro ao carregar o arquivo JSON:", error);
        mensagem.textContent = "Erro ao carregar os dados. Tente novamente.";
        mensagem.className = "error";
    }
}


function ircadastro(){

    setTimeout(() => {
        window.location.href = "cadastro.html"; // Substitua "dashboard.html" pelo caminho desejado
    }, 1000); // 2000ms = 2 segundos


}

