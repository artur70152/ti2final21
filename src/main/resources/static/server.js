const express = require('express');
const fs = require('fs');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());  // ← ESSA LINHA É OBRIGATÓRIA!

app.use(express.static('public')); // ou 'src/main/resources/static' → escolha uma!

// Servir arquivos estáticos da pasta "public"


// Rota para salvar dados no arquivo JSON
app.post('/save-user', (req, res) => {
    const newUser = req.body;

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).send('Erro ao ler o arquivo.');
        }

        let users = JSON.parse(data || '[]');
        const emailExists = users.some(user => user.email === newUser.email);

        if (emailExists) {
            return res.status(400).send('Erro: E-mail já está em uso.');
        }

        users.push(newUser);
        fs.writeFile('aa.json', JSON.stringify(users, null, 2), (err) => {
            if (err) {
                return res.status(500).send('Erro ao salvar o arquivo.');
            }
            res.status(200).send('Usuário salvo com sucesso!');
        });
    });
});
app.get('/get-user/:email', (req, res) => {
    const email = req.params.email;

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json({ message: 'Erro ao carregar os dados.' });
        }

        const users = JSON.parse(data || '[]');
        const user = users.find(user => user.email === email);

        if (!user) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }

        res.status(200).json(user);
    });
});


app.get('/get-all-artes', (req, res) => {
    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json({ message: 'Erro ao carregar os dados.' });
        }

        const users = JSON.parse(data);

        // Coleta todas as artes de todos os usuários, incluindo o índice
        const todasAsArtes = users.flatMap(user => 
            user.artes.map((arte, index) => ({
                email: user.email,
                indexa: index, // Adiciona o índice de cada arte
                ...arte       // Inclui as propriedades de cada arte
            }))
        );

        res.status(200).json(todasAsArtes); // Retorna todas as artes em um único array
    });
});






app.get('/get-profile/:email', (req, res) => {
    const email = req.params.email;
    
    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).send('Erro ao ler o arquivo.');
        }
        
        const users = JSON.parse(data);
        const user = users.find(user => user.email === email);

        if (!user) {
            return res.status(404).send('Usuário não encontrado.');
        }

        // Retorna apenas as informações de perfil (nome e foto)
        const profile = {
            name: user.name,
            foto: user.foto
        };
        
        res.status(200).json(profile);
    });
});


// Rota para obter artes de um usuário específico por e-mail
app.get('/get-artes/:email', (req, res) => {
    const email = req.params.email;

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).send('Erro ao ler o arquivo.');
        }

        let users = JSON.parse(data || '[]');
        const user = users.find(user => user.email === email);

        if (!user) {
            return res.status(404).send('Usuário não encontrado.');
        }

        res.status(200).json(user.artes);
    });
});

app.post('/doar/:email', (req, res) => {
    const email = req.params.email;
    const { donationValue } = req.body; // Valor enviado do frontend

    // Validação do valor da doação
    if (!donationValue || isNaN(donationValue) || donationValue <= 0) {
        return res.status(400).json({ message: 'Valor de doação inválido.' });
    }

    // Ler o arquivo JSON
    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            console.error('Erro ao ler o arquivo:', err);
            return res.status(500).json({ message: 'Erro ao acessar os dados.' });
        }

        let users = JSON.parse(data || '[]'); // Converte os dados para objeto

        // Encontra o usuário correspondente ao email
        const user = users.find(user => user.email === email);

        if (!user) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }

        // Atualiza o valor da doação
        user.doacoes = (user.doacoes || 0) + parseFloat(donationValue);

        // Escreve as alterações de volta no arquivo JSON
        fs.writeFile('aa.json', JSON.stringify(users, null, 2), 'utf8', (err) => {
            if (err) {
                console.error('Erro ao salvar o arquivo:', err);
                return res.status(500).json({ message: 'Erro ao salvar os dados.' });
            }

            res.status(200).json({ message: 'Doação registrada com sucesso.', user });
        });
    });
});


// Rota para adicionar um comentário
app.post('/add-comment', (req, res) => {
    const { email, arteIndex, comentario } = req.body;

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).send('Erro ao ler o arquivo.');
        }

        let users = JSON.parse(data || '[]');
        const user = users.find(user => user.email === email);
        //console.log(user);
       // console.log(user.artes);
        if (!user || !user.artes[arteIndex]) {
            return res.status(404).send('Usuário ou arte não encontrados.');
        }
       // console.log(user);
        // Adiciona o comentário
        user.artes[arteIndex].comentarios.push(comentario);
       // console.log(user);
        // Salva a atualização no arquivo JSON
        fs.writeFile('aa.json', JSON.stringify(users, null, 2), (err) => {
            if (err) {
                return res.status(500).send('Erro ao salvar o comentário.');
            }
            res.status(200).json({ message: 'Comentário adicionado com sucesso!' });
        });
    });
});
/*

app.get('/get-messages/:email', (req, res) => {
    const email = req.params.email;
    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) return res.status(500).send('Erro ao ler o arquivo.');
        const users = JSON.parse(data);
        const user = users.find(user => user.email === email);

        if (!user) return res.status(404).send('Usuário não encontrado.');

        // Retorna as mensagens do usuário
        res.status(200).json(user.mensagens || []);
    });
});
 */
app.get('/get-messages/:email', (req, res) => {
    const email = req.params.email;
    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) return res.status(500).send('Erro ao ler o arquivo.');
        
        const users = JSON.parse(data);
        const user = users.find(u => u.email === email);

        if (!user) return res.status(404).send('Usuário não encontrado.');

        // Mapeia as mensagens com o nome de cada usuário remetente
        const mensagens = user.mensagens.map(msg => {
          
            const remetente = users.find(u => u.email === msg.usuario);
            //console.log('Dados do remetente:', remetente.foto);
            return {
                ...msg,
                nome: remetente ? remetente.name : "Usuário Anônimo",
         foto:remetente.foto
            };
        });

        res.status(200).json(mensagens);
    });
});


// Rota para adicionar uma nova mensagem
app.post('/add-message', (req, res) => {
    const { usuario, texto, remetente } = req.body;

    if (!usuario || !texto || !remetente) {
        return res.status(400).json({ message: 'Usuário, texto e remetente são obrigatórios.' });
    }

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json({ message: 'Erro ao ler o arquivo.' });
        }

        const users = JSON.parse(data);
        const remetenteUser = users.find(user => user.email === remetente);
        const destinatarioUser = users.find(user => user.email === usuario);

        if (!remetenteUser || !destinatarioUser) {
            return res.status(404).json({ message: 'Usuário remetente ou destinatário não encontrado.' });
        }

        // Adiciona a mensagem no histórico do destinatário
        let conversa = destinatarioUser.mensagens.find(m => m.usuario === remetente);
        if (!conversa) {
            conversa = { nome: remetenteUser.name, usuario: remetente, mensagens: [] };
            destinatarioUser.mensagens.push(conversa);
        }
        conversa.mensagens.push(texto);

        // Adiciona a mensagem no histórico do remetente
        let conversaRemetente = remetenteUser.mensagens.find(m => m.usuario === usuario);
        if (!conversaRemetente) {
            conversaRemetente = { nome: destinatarioUser.name, usuario: usuario, mensagens: [] };
            remetenteUser.mensagens.push(conversaRemetente);
        }
        conversaRemetente.mensagens.push(`Você: ${texto}`);

        // Salva as mudanças no arquivo JSON
        fs.writeFile('aa.json', JSON.stringify(users, null, 2), err => {
            if (err) {
                return res.status(500).json({ message: 'Erro ao salvar a mensagem no arquivo.' });
            }

            res.status(200).json({ message: 'Mensagem enviada com sucesso!' });
        });
    });
});


app.post('/add-arte', (req, res) => {
    const { email, arte } = req.body; // Certifique-se de que o corpo da requisição contém esses campos

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json({ message: 'Erro ao ler o arquivo.' });
        }

        let users = JSON.parse(data || '[]');
        
        const user = users.find(user => user.email === email);
        
        if (!user) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }
        
        // Adiciona a nova arte ao campo 'artes'
        user.artes.push({
            arte: arte,
            name:extractBetweenHyphenAndDot(arte),
            curtidas:0,
            usuariosQueCurtiram:[],
            comentarios: []
        });

        // Salva as mudanças no JSON
        fs.writeFile('aa.json', JSON.stringify(users, null, 2), (err) => {
            if (err) {
                return res.status(500).json({ message: 'Erro ao salvar a arte.' });
            }
            res.status(200).json({ message: 'Arte adicionada com sucesso!' });
        });
    });
});

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
const multer = require('multer');

// Configuração do multer para salvar imagens na pasta "public/images"
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'public/images');
    },
    filename: (req, file, cb) => {
        cb(null, Date.now() + '-' + file.originalname);
    },
});

const upload = multer({ storage });

app.post('/upload', upload.single('file'), (req, res) => {
    if (!req.file) {
        return res.status(400).send('Nenhum arquivo enviado.');
    }

    // Retorna o caminho da imagem salva
    res.status(200).json({ filePath: `/images/${req.file.filename}` });
});
app.post('/update-profile-photo', (req, res) => {
    const { email, foto } = req.body;

    if (!email || !foto) {
        return res.status(400).json({ message: 'Email e foto são obrigatórios.' });
    }

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json({ message: 'Erro ao ler o arquivo.' });
        }

        let users = JSON.parse(data || '[]');
        const user = users.find(user => user.email === email);

        if (!user) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }

        // Atualiza a foto de perfil
        user.foto = foto;

        fs.writeFile('aa.json', JSON.stringify(users, null, 2), (err) => {
            if (err) {
                return res.status(500).json({ message: 'Erro ao salvar a foto de perfil.' });
            }
            res.status(200).json({ message: 'Foto de perfil atualizada com sucesso!' });
        });
    });
});
app.post('/update-curtidas', (req, res) => {
    const { email, arteIndex, usuario } = req.body;

    if (!email || arteIndex === undefined || !usuario) {
        return res.status(400).json({ message: 'Email, índice da arte e usuário são obrigatórios.' });
    }

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json({ message: 'Erro ao ler o arquivo.' });
        }

        let users = JSON.parse(data || '[]');
        const user = users.find(user => user.email === email);

        if (!user) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }

        const arte = user.artes[arteIndex];

        if (!arte) {
            return res.status(404).json({ message: 'Arte não encontrada.' });
        }

        if (!arte.usuariosQueCurtiram) {
            arte.usuariosQueCurtiram = [];
        }

        const userIndex = arte.usuariosQueCurtiram.indexOf(usuario);

        if (userIndex !== -1) {
            // Usuário já curtiu, remove a curtida
            arte.usuariosQueCurtiram.splice(userIndex, 1);
            arte.curtidas = Math.max(0, (arte.curtidas || 0) - 1);
        } else {
            // Usuário não curtiu, adiciona a curtida
            arte.usuariosQueCurtiram.push(usuario);
            arte.curtidas = (arte.curtidas || 0) + 1;
        }

        fs.writeFile('aa.json', JSON.stringify(users, null, 2), (err) => {
            if (err) {
                return res.status(500).json({ message: 'Erro ao salvar as curtidas.' });
            }
            res.status(200).json({
                message: userIndex !== -1 ? 'Curtida removida com sucesso!' : 'Curtida registrada com sucesso!',
                curtidas: arte.curtidas,
            });
        });
    });
});



app.post('/remove-arte', (req, res) => {
    const { email, arteIndex } = req.body;

    if (!email || arteIndex === undefined) {
        return res.status(400).json({ message: 'Email e índice da arte são obrigatórios.' });
    }

    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json({ message: 'Erro ao ler o arquivo.' });
        }

        let users = JSON.parse(data || '[]');
        const user = users.find(user => user.email === email);

        if (!user) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }

        if (!user.artes || !user.artes[arteIndex]) {
            return res.status(404).json({ message: 'Arte não encontrada.' });
        }

        // Remove a arte do array
        user.artes.splice(arteIndex, 1);

        // Salva o JSON atualizado
        fs.writeFile('aa.json', JSON.stringify(users, null, 2), (err) => {
            if (err) {
                return res.status(500).json({ message: 'Erro ao salvar o arquivo.' });
            }
            res.status(200).json({ message: 'Arte removida com sucesso!' });
        });
    });
});

app.post('/save-user', (req, res) => {
    const newUser = req.body;

    // Ler o arquivo aa.json existente
    fs.readFile('aa.json', 'utf8', (err, data) => {
        if (err) {
            return res.status(500).send('Erro ao ler o arquivo.');
        }

        let users = JSON.parse(data || '[]'); // Se o arquivo estiver vazio, usar array vazio
        
        // Verificar se o email já existe
        const emailExists = users.some(user => user.email === newUser.email);
        
        if (emailExists) {
            return res.status(400).send('Erro: E-mail já está em uso.');
        }

        // Adicionar o novo usuário se o email não existir
        users.push(newUser);

        // Salva o novo conteúdo no arquivo
        fs.writeFile('aa.json', JSON.stringify(users, null, 2), (err) => {
            if (err) {
                return res.status(500).send('Erro ao salvar o arquivo.');
            }
            res.status(200).send('Usuário salvo com sucesso!');
        });
    });
});

// Iniciar o servidor
app.listen(3000, () => {
    console.log('Servidor rodando na porta 3000');
});
