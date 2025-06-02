const db = require('../database');

async function criarUsuario({ name, email, senha, foto }) {
  const [result] = await db.query(
    'INSERT INTO usuarios (name, email, senha, foto) VALUES (?, ?, ?, ?)',
    [name, email, senha, foto]
  );
  return result.insertId;
}

async function getUsuarioPorEmail(email) {
  const [rows] = await db.query('SELECT * FROM usuarios WHERE email = ?', [email]);
  return rows[0];
}

module.exports = { criarUsuario, getUsuarioPorEmail };
