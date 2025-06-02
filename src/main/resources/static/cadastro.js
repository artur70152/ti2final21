document.getElementById('userForm').addEventListener('submit', function(event) {
    event.preventDefault();

    // Coleta os dados do formulário
    let name = document.getElementById('name').value;
    let email = document.getElementById('email').value;
    let senha = document.getElementById('senha').value;

    // Cria um objeto de usuário
    let user = {
        name: name,
        normal:true,
        doacoes:0,
        email: email,
        senha: senha,
        foto: "",
        artes: [{
            arte:"",
            name: "",
            curtidas:0,
            usuariosQueCurtiram:[],
            comentarios:[{
                usuario:"",
                texto:""
            }]
        }],
        mensagens: [{
            usuario: "",
            mensagens: []
        }]
    };

    // Salvar usuário no localStorage
    saveUserToLocal(user);

    // Enviar os dados para o servidor usando fetch
    fetch('/save-user', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
    .then(response => response.json())
    .then(data => {
        alert(data); // Mensagem de sucesso
        document.getElementById('userForm').reset(); // Limpa o formulário
            console.log(data+" "+"ddd");
if (data.message==="Usuário salvo com sucesso!") {

    setTimeout(() => {
             window.location.href = `pp.html?email=${encodeURIComponent(email)}`; // Substitua "dashboard.html" pelo caminho desejado
         }, 1000); // 2000ms = 2 segundos
}
        
    })
    .catch(error => console.error('Erro:', error));


  
});

// Função para salvar o usuário no localStorage
function saveUserToLocal(user) {
    fetch('/users')
      .then(response => response.json())
      .then(users => {
        console.log('Usuários existentes:', users);

        // Verifica se o e-mail já existe
        const emailExists = users.some(existingUser => existingUser.email === user.email);

        if (!emailExists) {
            // ✅ Aqui você poderia salvar no localStorage, se ainda quiser manter.
            // localStorage.setItem('users', JSON.stringify([...users, user]));
            console.log('Usuário disponível para cadastro:', user);
        } else {
            alert('Erro: E-mail já está em uso no banco de dados.');
            console.log('E-mail já existe:', user.email);
        }
      })
      .catch(error => console.error('Erro ao buscar usuários:', error));
}
