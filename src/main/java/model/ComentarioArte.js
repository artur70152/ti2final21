import Sequelize, { Model } from 'sequelize';

class ComentarioArte extends Model {
  static init(sequelize) {
    super.init(
      {
        usuario: Sequelize.STRING,
        texto: Sequelize.STRING,
      },
      {
        sequelize,
        tableName: 'comentario_arte',
        underscored: true,
      }
    );

    return this;
  }

  static associate(models) {
    this.belongsTo(models.Arte, {
      foreignKey: 'arte_id',
      as: 'arteReferente'
    });
  }
}

export default ComentarioArte;
