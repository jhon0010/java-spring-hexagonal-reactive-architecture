CREATE TABLE stock_company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    stock_symbol VARCHAR(10) NOT NULL
);

-- Optional: Add indexes for better performance
CREATE INDEX idx_stock_company_symbol ON stock_company(stock_symbol);
CREATE INDEX idx_stock_company_name ON stock_company(name);

-- Optional: Add unique constraint on stock symbol
ALTER TABLE stock_company ADD CONSTRAINT uk_stock_symbol UNIQUE (stock_symbol);

INSERT INTO stock_company (name, description, stock_symbol) VALUES
('Bitcoin', 'Decentralized digital cryptocurrency', 'BTC'),
('Ethereum', 'Smart contract platform and cryptocurrency', 'ETH'),
('Litecoin', 'Peer-to-peer cryptocurrency created from Bitcoin', 'LTC'),
('Cardano', 'Proof-of-stake blockchain platform', 'ADA'),
('Polkadot', 'Multi-chain interoperability protocol', 'DOT'),
('Solana', 'High-performance blockchain platform', 'SOL'),
('Ripple', 'Digital payment protocol and cryptocurrency', 'XRP'),
('Dogecoin', 'Peer-to-peer digital currency', 'DOGE'),
('Avalanche', 'Open-source platform for decentralized applications', 'AVAX'),
('Chainlink', 'Decentralized oracle network', 'LINK');