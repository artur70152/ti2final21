import Mensagem from '../models/Mensagem';
import Usuario from '../models/Usuario';

class MensagemController {
   async addMessage(req, res) {
    const { usuario, texto, remetente } = req.body; 

    try {
      // Buscar destinatário (quem vai receber)
      const destinatario = await Usuario.findOne({ where: { email: usuario } });
      // Buscar remetente (quem está enviando)
      const remetenteUsuario = await Usuario.findOne({ where: { email: remetente } });

      if (!destinatario || !remetenteUsuario) {
        return res.status(404).json({ error: 'Usuário remetente ou destinatário não encontrado' });
      }

      // Procurar por conversa entre esse remetente e esse destinatário
      let conversa = await Mensagem.findOne({
        where: { usuario: usuario, nome: remetenteUsuario.name }
      });

      if (!conversa) {
        // Criar nova conversa
        conversa = await Mensagem.create({
          usuario: usuario,
          usuario_id: destinatario.id,
          mensagens: [],
          nome: remetenteUsuario.name,  // Nome do remetente
          foto: remetenteUsuario.foto   // Foto do remetente
        });
      }

      // Nova mensagem
      const novaMensagem = {
        remetente: remetente,
        texto: texto,
        timestamp: new Date()
      };

      // Atualizar mensagens
      const mensagensAtualizadas = [...(conversa.mensagens || []), novaMensagem];

      await conversa.update({ mensagens: mensagensAtualizadas });

      return res.json({ success: true, mensagem: novaMensagem });

    } catch (err) {
      console.error(err);
      return res.status(500).json({ error: 'Erro ao enviar mensagem' });
    }
  }

  async getConversation(req, res) {
    const { usuario, destinatario } = req.params;

    try {
      // Conversas: de remetente para destinatário, e vice-versa
      const conversa1 = await Mensagem.findOne({ where: { usuario, nome: destinatario } });
      const conversa2 = await Mensagem.findOne({ where: { usuario: destinatario, nome: usuario } });

      const mensagens = [];

      if (conversa1) mensagens.push(...conversa1.mensagens);
      if (conversa2) mensagens.push(...conversa2.mensagens);

      mensagens.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

      return res.json(mensagens);
    } catch (err) {
      console.error(err);
      return res.status(500).json({ error: 'Erro ao buscar conversa' });
    }
  }

}

export default new MensagemController();
