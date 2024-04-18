package com.puc.pi3_es_2024_t24

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.TypeConverters

@Dao
@TypeConverters
interface ClientDao {
    @Query("SELECT * FROM client") // PUXAR TODOS OS CLIENTES
    fun getAll(): List<Client>

    @Query("SELECT * FROM client WHERE cpf LIKE :cpfClient") // PUXAR DETERMINADO CLIENTE COM BASE NO CPF
    fun getClientByCpf(cpfClient: String): Client

    @Insert
    fun insertAll(vararg clients: Client) // INSERIR TODOS NOVO CLIENTE

    @Insert
    fun insert(vararg client: Client) // INSERIR NOVO CLIENTE

    @Delete
    fun delete(client: Client) // DELETAR UM DETERMINADO CLIENTE
}