import Sequelize, { Model } from 'sequelize';

class CurtidaArte extends Model {
  static init(sequelize) {
    super.init(
      {
        usuario: Sequelize.STRING,
        arte_id: Sequelize.INTEGER, // ✅ Opcional, só pra clareza.
      },
      {
        sequelize,
        tableName: 'curtidaarte',
        underscored: true,
        timestamps: true  // ✅ Essencial!
      }
    );

    return this;
  }

  static associate(models) {
    this.belongsTo(models.Arte, {
      foreignKey: 'arte_id',
      as: 'arteCurtida'
    });
  }
}

export default CurtidaArte;
