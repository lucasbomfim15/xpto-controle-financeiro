-- ================================================
-- Function: fn_calcula_saldo_cliente
-- Description: Calcula o saldo total do cliente considerando
--             transações de crédito e débito e saldo inicial.
-- ================================================

CREATE OR REPLACE FUNCTION fn_calcula_saldo_cliente(p_customer_id UUID)
RETURNS NUMERIC AS $$
DECLARE
v_saldo NUMERIC := 0;
BEGIN
    -- Soma transações (positivas e negativas)
SELECT COALESCE(SUM(
                        CASE
                            WHEN t.type = 'CREDIT' THEN t.amount
                            WHEN t.type = 'DEBIT' THEN -t.amount
                            ELSE 0
                            END
                ), 0)
INTO v_saldo
FROM transactions t
         INNER JOIN accounts a ON t.account_id = a.id
WHERE a.customer_id = p_customer_id;

-- Soma os saldos iniciais das contas
SELECT v_saldo + COALESCE(SUM(a.initial_balance), 0)
INTO v_saldo
FROM accounts a
WHERE a.customer_id = p_customer_id;

RETURN v_saldo;
END;
$$ LANGUAGE plpgsql;

-- ================================================
-- Function: fn_enderecos_cliente
-- Description: Retorna os endereços vinculados a um cliente.
-- ================================================

CREATE OR REPLACE FUNCTION fn_enderecos_cliente(p_customer_id UUID)
RETURNS TABLE (
    id UUID,
    street VARCHAR,
    city VARCHAR,
    state CHAR(2),
    zip_code VARCHAR,
    customer_id UUID
) AS $$
BEGIN
RETURN QUERY
SELECT
    a.id,
    a.street,
    a.city,
    a.state,
    a.zip_code,
    a.customer_id
FROM addresses a
WHERE a.customer_id = p_customer_id;
END;
$$ LANGUAGE plpgsql;
