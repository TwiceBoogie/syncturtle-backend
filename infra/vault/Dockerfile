FROM hashicorp/vault:1.20.4

USER root

RUN apk add --no-cache curl

COPY ./infra/vault/policies /vault/policies

COPY ./infra/db/rotate.sql /vault/sql/rotate.sql

COPY ./infra/vault/bin/docker-entrypoint-vault.sh /bin/docker-entrypoint-vault.sh

RUN chmod +x /bin/docker-entrypoint-vault.sh

USER vault
EXPOSE 8200
ENTRYPOINT [ "/bin/docker-entrypoint-vault.sh" ]