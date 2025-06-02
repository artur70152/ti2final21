const db = require('../database');

async function criarArte({ usuario_id, arte, name, curtidas }) {
  const [result] = await db.query(
    'INSERT INTO artes (usuario_id, arte, name, curtidas) VALUES (?, ?, ?, ?)',
    [usuario_id, arte, name, curtidas || 0]
  );
  return result.insertId;
}

async function getArtesPorUsuario(usuario_id) {
  const [rows] = await db.query(
    'SELECT * FROM artes WHERE usuario_id = ?', 
    [usuario_id]
  );
  return rows;
}

async function removerArte(arte_id) {
  const [result] = await db.query(
    'DELETE FROM artes WHERE id = ?', 
    [arte_id]
  );
  return result.affectedRows;
}

async function getTodasAsArtes() {
  const [rows] = await db.query(
    `SELECT a.*, u.email AS usuario_email FROM artes a
     JOIN usuarios u ON a.usuario_id = u.id`
  );
  return rows;
}

module.exports = {
  criarArte,
  getArtesPorUsuario,
  removerArte,
  getTodasAsArtes
};
