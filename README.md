# PI3-ES-2024-T24

## LockN'Live

*LockN'Live is an app intended for renting lockers across the region in an easy and interactive way*

## Contents

## Firebase Firestore
* Collection "pessoas"
* Colletion "armários"
* Collection "locações"
* Colletion "gerentes"
  
# ENDPOINTS

## CLIENTES 

**ADICIONAR CLIENTE**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/addClient

> CORPO DO BODY 
```
{
    nome,
    cpf,
    dataNascimento,
    celular,
    email
}
```

**DELETAR CLIENTE**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/delClient

> CORPO DO BODY 
```
{
    cpf
}
```

**ATUALIZAR CLIENTE**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/updateClient

> CORPO DO BODY 
```
{
    cpf,
    nome,
    dataNascimento,
    celular,
    email
}
```

**PUXAR CLIENTE ESPECÍFICO**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getClient

> CORPO DA QUERY
```
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getClient?email={EMAIL DO CLIENTE}
```

**PUXAR TODOS OS CLIENTES**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getAllClients

**ALTERAR CARTÃO**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/updateCard

> CORPO DO BODY
```
{
    cpf,
    numero,
    cvv,
    validade
}
```

## UNIDADES

**PUXAR DETERMINADA UNIDADE**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getUnity

> CORPO DA QUERY
```
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getUnity?unityId={ID DA UNIDADE}
```

**PUXAR TODAS AS UNIDADES**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getUnities

## LOCAÇÕES

**PUXAR TODAS AS LOCAÇÕES DE DETERMINADA UNIDADE**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getUnityLocations

> CORPO DA QUERY
```
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/getUnityLocations?unityId={ID DA UNIDADE}
```

**ATUALIZAR DETERMINADA LOCAÇÃO**
https://southamerica-east1-projetointegrador3-416713.cloudfunctions.net/updateLocation

> CORPO DO BODY
```
{
    locationId,
    status,
    horaFinal, // OPCIONAL (SOMENTE QUANDO FOR ALUGADA)
    clienteCpf, // OPCIONAL (SOMENTE QUANDO FOR ALUGADA)
}
```
 
