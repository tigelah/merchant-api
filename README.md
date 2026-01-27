# merchant-api

API HTTP voltada ao **merchant** (loja/e-commerce) para integrar com o simulador de adquirência.

Este serviço é um **façade/BFF**: ele **expõe endpoints amigáveis** para o merchant e **encaminha** as chamadas para o `acquirer-core`, preservando:
- `Idempotency-Key` (autorização)
- `correlationId` (header `X-Correlation-Id`, se enviado)

## Propósito
- Fornecer uma porta de entrada “merchant-friendly” sem expor detalhes internos do `acquirer-core`.
- Padronizar respostas/erros.
- Facilitar testes E2E (um endpoint para o merchant e o restante via eventos).

## Endpoints

### 1) Autorizar pagamento
`POST /v1/payments/authorize`

Headers:
- `Idempotency-Key` (obrigatório)
- `X-Correlation-Id` (opcional)

Respostas esperadas:
- **200 OK** com o pagamento (idempotente: mesma chave retorna o mesmo `paymentId`)
- **422 Unprocessable Entity** se o cartão for rejeitado
- **400 Bad Request** se payload inválido

### 2) Capturar pagamento
`POST /v1/payments/{paymentId}/capture`

### 3) Consultar pagamento
`GET /v1/payments/{paymentId}`

## Health e métricas
- `GET /actuator/health`
- `GET /actuator/prometheus`

## Como rodar

### Docker
```bash
docker network create acquiring-net || true
docker compose up --build
```

### Local (Maven)
```bash
mvn clean spring-boot:run
```

## Testes e cobertura
```bash
mvn clean verify
```
Relatório: `target/site/jacoco/index.html`