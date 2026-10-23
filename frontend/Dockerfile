# build
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json .
COPY package-lock.json .
RUN npm ci
COPY . .
ENV VITE_API_BASE_URL=/api
RUN npm run build

# run
FROM nginx:1.30-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
