CREATE TABLE IF NOT EXISTS transactions
(
    id     SERIAL PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    data   JSON
);

INSERT INTO transactions (id, amount, data) VALUES
(123, 100.05, '{"a":1,"b":2}'::json),
(124, 150.75, '{"a":10,"b":20}'::json),
(125, 1010.00, '{"a":20,"b":30}'::json),
(126, 15.5, '{"a":30,"b":40}'::json)
ON CONFLICT DO NOTHING;