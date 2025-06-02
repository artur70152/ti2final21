import Sequelize, { Model } from 'sequelize';

class Arte extends Model {
  static init(sequelize) {
    super.init(
      {
        usuario_id: Sequelize.INTEGER,
        arte: Sequelize.STRING,
        name: Sequelize.STRING,
        curtidas: Sequelize.INTEGER
      },
      {
        sequelize,
        tableName: 'arte',
        underscored: true,   // ✅ Para usar snake_case: created_at, updated_at
        timestamps: true     // ✅ Porque sua tabela tem created_at e updated_at
      }
    );

    return this;
  }
static associate(models) {
  this.belongsTo(models.Usuario, { foreignKey: 'usuario_id', as: 'usuario' });
  this.hasMany(models.ComentarioArte, { foreignKey: 'arte_id', as: 'comentarios' });
}


}

export default Arte;
