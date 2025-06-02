const Usuario = require('../models/Usuario');
const Arte = require('../models/Arte');

const UserController = {
  async store(req, res) {
    const { name, email, senha, foto } = req.body;
    try {
      const id = await Usuario.criarUsuario({ name, email, senha, foto });
      return res.status(201).json({ id, message: "Usuário salvo com sucesso!" });
    } catch (err) {
      console.error(err);
      return res.status(500).json({ error: 'Erro ao salvar usuário.' });
    }
  },

  async getUserByEmail(req, res) {
    const { email } = req.params;
    try {
      const user = await Usuario.getUsuarioPorEmail(email);
      if (!user) return res.status(404).json({ error: 'Usuário não encontrado.' });
      return res.status(200).json(user);
    } catch (err) {
      return res.status(500).json({ error: 'Erro ao buscar usuário.' });
    }
  },

  async getAllArtes(req, res) {
    try {
      const artes = await Arte.getTodasAsArtes();
      return res.json(artes);
    } catch (err) {
      return res.status(500).json({ error: 'Erro ao buscar artes.' });
    }
  }
};

module.exports = UserController;
