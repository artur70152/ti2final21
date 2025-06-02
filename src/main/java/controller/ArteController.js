import Usuario from '../models/Usuario';
import Arte from '../models/Arte';

import CurtidaArte from '../models/CurtidaArte';

import ComentarioArte from '../models/ComentarioArte';
class ArteController {

  async popularArtes(req, res) {
    try {
      const usuarios = await Usuario.findAll();

      const imagens = [
        '/images/arte1.jpg',
        '/images/arte2.jpg',
        '/images/arte3.jpg'
      ];

      for (const usuario of usuarios) {
        for (let i = 0; i < 3; i++) {
          await Arte.create({
            usuario_id: usuario.id,
            arte: imagens[i % imagens.length],  // ✅ Alterna entre as imagens
            name: `Arte ${i + 1} de ${usuario.name}`,
            curtidas: 0
          });
        }
      }

      return res.status(200).json({ message: 'Banco populado com 3 artes para cada usuário.' });
    } catch (error) {
      console.error('Erro ao popular banco:', error);
      return res.status(500).json({ error: 'Erro ao popular banco com artes.' });
    }
  }
async updateAddArte(req, res) {
    const { email, arte, name, curtidas, usuariosQueCurtiram } = req.body;

    try {
        // Busca o usuário pelo email
        const usuario = await Usuario.findOne({ where: { email } });

        if (!usuario) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }

        // Cria nova arte vinculada ao usuario
        const novaArte = await Arte.create({
            usuario_id: usuario.id,
            arte,
            name: name || '',
            curtidas: curtidas || 0,
            usuariosQueCurtiram: usuariosQueCurtiram || []
        });

        return res.status(201).json({ message: 'Arte adicionada com sucesso.', novaArte });
    } catch (error) {
        console.error('Erro ao adicionar arte:', error);
        return res.status(500).json({ error: 'Erro ao adicionar arte.' });
    }
}

 async uploadFile(req, res) {
    try {
      if (!req.file) {
        return res.status(400).json({ error: 'Nenhum arquivo enviado.' });
      }

      const filePath = `/uploads/${req.file.filename}`;
      return res.status(200).json({ filePath });
    } catch (error) {
      console.error('Erro ao fazer upload:', error);
      return res.status(500).json({ error: 'Erro ao fazer upload do arquivo.' });
    }
  }


async updateCurtidas(req, res) {
  const { email, arteIndex, usuario } = req.body;

  try {
    const user = await Usuario.findOne({ where: { email } });
    if (!user) return res.status(404).json({ error: 'Usuário não encontrado' });

    const arte = await Arte.findByPk(arteIndex);
    if (!arte) return res.status(404).json({ error: 'Arte não encontrada' });

    const curtida = await CurtidaArte.findOne({
      where: { arte_id: arte.id, usuario }
    });

    if (curtida) {
      // ✅ Só permite descurtir se o mesmo usuário fez a curtida
      if (curtida.usuario !== usuario) {
        return res.status(403).json({ error: 'Você não pode descurtir esta arte pois não foi você quem curtiu.' });
      }

      await curtida.destroy();
      arte.curtidas = Math.max(arte.curtidas - 1, 0);
      await arte.save();
      return res.status(200).json({ message: 'Descurtido', curtidas: arte.curtidas });
    } else {
      // ✅ Não curtiu ainda → curtir
      await CurtidaArte.create({ arte_id: arte.id, usuario });
      arte.curtidas += 1;
      await arte.save();
      return res.status(200).json({ message: 'Curtido', curtidas: arte.curtidas });
    }
  } catch (err) {
    console.error('Erro ao atualizar curtidas:', err);
    return res.status(500).json({ error: 'Erro ao atualizar curtidas' });
  }
}

async getArtesByEmail(req, res) {
  const { email } = req.params;

  try {
    const usuario = await Usuario.findOne({ where: { email } });

    if (!usuario) {
      return res.status(404).json({ error: 'Usuário não encontrado' });
    }

    const artes = await Arte.findAll({
      where: { usuario_id: usuario.id },
      include: [
        {
          model: ComentarioArte,
          as: 'comentarios',
          attributes: ['usuario', 'texto']
        }
      ]
    });

    return res.json(artes);
  } catch (error) {
    console.error('Erro ao buscar artes:', error);
    return res.status(500).json({ error: 'Erro ao buscar artes' });
  }
}

async removeArte(req, res) {
    const { arteId } = req.body;

    try {
        const arte = await Arte.findByPk(arteId);

        if (!arte) {
            return res.status(404).json({ message: 'Arte não encontrada.' });
        }

        await arte.destroy();

        return res.status(200).json({ message: 'Arte removida com sucesso.' });
    } catch (error) {
        console.error('Erro ao remover arte:', error);
        return res.status(500).json({ error: 'Erro ao remover arte.' });
    }
}



async addComment(req, res) {
    const { arteId, usuario, texto } = req.body;

    try {
        const comentario = await ComentarioArte.create({
            arte_id: arteId,
            usuario,
            texto
        });

        return res.status(201).json(comentario);
    } catch (err) {
        console.error("Erro ao adicionar comentário:", err);
        return res.status(500).json({ error: 'Erro ao adicionar comentário' });
    }
}

async getAllArtes(req, res) {
  try {
    const artes = await Arte.findAll({
      include: [
        {
          model: Usuario,
          as: 'usuario',
          attributes: ['email']
        },
        {
          model: ComentarioArte,
          as: 'comentarios',
          attributes: ['usuario', 'texto']
        }
      ]
    });

    return res.json(artes);
  } catch (error) {
    console.error('Erro ao buscar todas as artes:', error);
    return res.status(500).json({ error: 'Erro ao buscar artes' });
  }
}

}

export default new ArteController();
