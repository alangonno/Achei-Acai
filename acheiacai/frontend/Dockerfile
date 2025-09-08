
FROM node:20-alpine AS builder

# Define o diretório de trabalho dentro do contentor
WORKDIR /app

COPY package*.json ./


RUN npm install

# Copia o resto do código-fonte da nossa aplicação
COPY . .

RUN npm run build

FROM nginx:stable-alpine

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]