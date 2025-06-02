import { Model, DataTypes } from 'sequelize';

class Usuario extends Model {
  static init(sequelize) {
    super.init(
      {
        name: DataTypes.TEXT,
        normal: DataTypes.BOOLEAN,
        doacoes: DataTypes.INTEGER,
        email: DataTypes.TEXT,
        senha: DataTypes.TEXT,
        foto: DataTypes.TEXT,
      },
      {
        sequelize,
        modelName: 'Usuario',
        tableName: 'usuario',
        timestamps: true, // Se quiser created_at e updated_at
      }
    );

    return this;
  }
static associate(models) {
  this.hasMany(models.Arte, { foreignKey: 'usuario_id', as: 'artes' });
  this.hasMany(models.Mensagem, { foreignKey: 'usuario_id', as: 'mensagens' }); 
}


}

export default Usuario;
