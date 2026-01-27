# ADR 0001 — merchant-api como façade (BFF) sobre acquirer-core

- Status: **Aceito**
- Data: 2026-01-24
- Serviço: `merchant-api`

## Contexto
O `acquirer-core` concentra regras e integrações. Precisamos de uma API voltada ao merchant com contrato estável, escondendo detalhes internos.

## Decisão
Criar `merchant-api` como façade:
- expõe `/v1/...`
- valida payloads
- encaminha para `acquirer-core` via HTTP
- repassa `Idempotency-Key` e `X-Correlation-Id`

## Consequências
- + contrato único e estável para o merchant
- + melhora DX e reduz acoplamento
- - adiciona hop de rede e mapeamento de erros
