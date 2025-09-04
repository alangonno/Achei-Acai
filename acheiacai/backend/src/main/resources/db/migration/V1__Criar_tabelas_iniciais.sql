CREATE TABLE usuarios (
id BIGINT AUTO_INCREMENT NOT NULL,
nome_usuario VARCHAR(100) NOT NULL,
senha_hash VARCHAR(60) NOT NULL,
funcao ENUM('ADMIN', 'OPERADOR') NOT NULL,

PRIMARY KEY (id),
UNIQUE (nome_usuario)
);

CREATE TABLE produtos (
id bigint AUTO_INCREMENT NOT NULL,
nome VARCHAR(60) NOT NULL,
tipo ENUM ('ACAI', 'SORVETE', 'SANDUICHE', 'SUCO', 'WHEY', 'BEBIDA', 'OUTRO')  NOT NULL ,
variacao VARCHAR(60) NOT NULL,
tamanho VARCHAR(20),
preco DECIMAL(10,2) NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE complementos (
id bigint AUTO_INCREMENT NOT NULL,
nome VARCHAR(40) NOT NULL,
preco_adicional  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
PRIMARY KEY(id),
UNIQUE (nome)
);

CREATE TABLE coberturas (
id bigint AUTO_INCREMENT NOT NULL,
nome VARCHAR(40) NOT NULL,
preco_adicional DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
PRIMARY KEY(id),
UNIQUE (nome)
);

CREATE TABLE vendas (
id bigint AUTO_INCREMENT NOT NULL,
data_venda DATETIME NOT NULL,
valor_total DECIMAL(10,2) NOT NULL,
forma_pagamento ENUM('PIX', 'CREDITO', 'DEBITO', 'DINHEIRO') NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE venda_itens (
id bigint AUTO_INCREMENT  NOT NULL,
venda_id bigint NOT NULL,
produto_id bigint NOT NULL,
quantidade bigint NOT NULL,
preco_unitario_da_venda DECIMAL(10,2),
PRIMARY KEY (id),
CONSTRAINT fk_venda_itens_vendas FOREIGN KEY (venda_id) REFERENCES vendas (id) ON DELETE CASCADE,
CONSTRAINT fk_venda_itens_produtos FOREIGN KEY (produto_id) REFERENCES produtos (id)
);

CREATE TABLE venda_item_complementos (
venda_item_id bigint NOT NULL,
complemento_id bigint NOT NULL,
quantidade INT NOT NULL DEFAULT 1,
PRIMARY KEY (venda_item_id, complemento_id),
CONSTRAINT fk_vic_venda_itens FOREIGN KEY (venda_item_id) REFERENCES venda_itens (id) ON DELETE CASCADE,
CONSTRAINT fk_vic_complementos FOREIGN KEY (complemento_id) REFERENCES complementos (id)
);

CREATE TABLE venda_item_coberturas (
venda_item_id bigint NOT NULL,
cobertura_id bigint NOT NULL,
quantidade INT NOT NULL DEFAULT 1,
PRIMARY KEY (venda_item_id, cobertura_id),
CONSTRAINT fk_vicob_venda_itens FOREIGN KEY (venda_item_id) REFERENCES venda_itens (id) ON DELETE CASCADE,
CONSTRAINT fk_vicob_coberturas FOREIGN KEY (cobertura_id) REFERENCES coberturas (id)
);