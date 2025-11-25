FROM php:8.4-apache

RUN apt-get update; \
    apt-get install -y --no-install-recommends ca-certificates; \
    docker-php-ext-install pdo pdo_mysql curl; \
    pecl install apcu; \
    docker-php-ext-enable apcu; \
    rm -rf /var/lib/apt/lists/*

COPY src/ /var/www/html/
RUN chown -R www-data:www-data /var/www/html
