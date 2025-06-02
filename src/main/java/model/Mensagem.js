import Sequelize, { Model } from 'sequelize';

class Mensagem extends Model {
  static init(sequelize) {
    super.init(
      {
        usuario: Sequelize.STRING,
        mensagens: Sequelize.JSONB,
        nome: Sequelize.STRING,
        foto: Sequelize.STRING,
      },
      {
        sequelize,
        tableName: 'mensagem',
        underscored: true,
      }
    );

    return this;
  }

  static associate(models) {
    this.belongsTo(models.Usuario, {
      foreignKey: 'usuario_id',
      as: 'usuarioReferente'
    });
  }
}

export default Mensagem;
