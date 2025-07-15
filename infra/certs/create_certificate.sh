#!/usr/bin/env bash

set -euo pipefail

# Set up directory variables
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CERTIFICATE_ROOT="$PROJECT_ROOT/infra/certs"
VAULT_TLS_DIR="$PROJECT_ROOT/infra/vault/tls"

ROOT_CA_DIR="$CERTIFICATE_ROOT/root"
KEYSTORE_FILE="$CERTIFICATE_ROOT/keystore.jks"

# Clean up old directories and files if they exist
rm -Rf "${ROOT_CA_DIR}" "${VAULT_TLS_DIR}" "${KEYSTORE_FILE}"

# Check dependencies
command -v openssl >/dev/null || { echo "[ERROR] openssl not found"; exit 1; }
KEYTOOL=keytool
[ -x "${KEYTOOL}" ] || KEYTOOL="${JAVA_HOME}/bin/keytool"
[ -x "${KEYTOOL}" ] || { echo "[ERROR] keytool not found"; exit 1; }

# Create necessary directories
mkdir -p "${ROOT_CA_DIR}"/{private,certs,newcerts} "${VAULT_TLS_DIR}"

# Generate Root CA
echo "[INFO] Generating Root CA private key"
openssl genrsa -aes256 -out "${ROOT_CA_DIR}/private/ca.key.pem" 4096

echo "[INFO] Generating Root CA certificate"
openssl req -config "${CERTIFICATE_ROOT}/root_openssl.cnf" \
  -key "${ROOT_CA_DIR}/private/ca.key.pem" \
  -new -x509 -days 7300 -sha256 -extensions v3_ca \
  -out "${ROOT_CA_DIR}/certs/ca.cert.pem"

touch "${ROOT_CA_DIR}/index.txt"
echo 1000 > "${ROOT_CA_DIR}/serial"

# Issue a manual TLS cert for Vault server to bootstrap HTTPS (no mTLS)
echo "[INFO] Generating Vault server private key"
openssl genrsa -out "${VAULT_TLS_DIR}/vault.key.pem" 2048

echo "[INFO] Generating CSR for Vault server"
openssl req -config "${CERTIFICATE_ROOT}/root_openssl.cnf" \
  -key "${VAULT_TLS_DIR}/vault.key.pem" \
  -new -sha256 \
  -out "${VAULT_TLS_DIR}/vault.csr.pem" \
  -subj "/C=US/ST=Wisconsin/L=Portage/O=twiceb/OU=Vault/CN=vault.twiceb.internal"

echo "[INFO] Signing Vault server CSR with Root CA"
openssl ca -config "${CERTIFICATE_ROOT}/root_openssl.cnf" \
  -extensions server_cert -days 375 -notext -md sha256 \
  -in "${VAULT_TLS_DIR}/vault.csr.pem" \
  -out "${VAULT_TLS_DIR}/vault.cert.pem" \
  -batch

# Create CA chain
cat "${ROOT_CA_DIR}/certs/ca.cert.pem" > "${VAULT_TLS_DIR}/ca_chain.pem"

# Import Root CA into Java keystore
echo "[INFO] Importing Root CA into Java keystore"
"${KEYTOOL}" -importcert -keystore "${KEYSTORE_FILE}" \
  -alias rootCA -file "${ROOT_CA_DIR}/certs/ca.cert.pem" \
  -noprompt -storepass changeit

echo "[INFO] Bootstrap TLS cert created. Start Vault using this cert."
echo "After Vault is unsealed, run the following to create Vault's intermediate:"
echo ""
echo "vault secrets enable -path=pki_int pki"
echo "vault write -field=csr pki_int/intermediate/generate/internal common_name="twiceb Intermediate CA" ttl=43800h > pki_int.csr.pem"
echo "openssl ca -config ${CERTIFICATE_ROOT}/root_openssl.cnf -extensions v3_intermediate_ca -days 43800 -notext -md sha256 -in pki_int.csr.pem -out intermediate.cert.pem"
echo "vault write pki_int/intermediate/set-signed certificate=@intermediate.cert.pem"

# run these commands first
# vault server -config=infra/vault/setup/setup.hcl
# export VAULT_ADDR="https://vault.twiceb.internal:8200"
# export VAULT_CACERT="$(pwd)/infra/certs/root/certs/ca.cert.pem"
# vault operator init
# vault secrets tune -max-lease-ttl=87600h pki_int
# vault write -field=csr pki_int/intermediate/generate/internal \
#   common_name="twiceb Intermediate CA" \
#   ttl=87600h \
#   country="US" \
#   organization="twiceb" \
#   province="Wisconsin" > pki_int.csr.pem

# openssl ca \
#   -config infra/certs/root_openssl.cnf \
#   -extensions v3_intermediate_ca \
#   -days 43800 \
#   -notext \
#   -md sha256 \
#   -in pki_int.csr.pem \
#   -out intermediate.cert.pem


# # e= exit immediately if command exits with non-zero status
# # u= treat unset var as an error and exit
# # o= command in pipeline fails the whole thng fails
# set -euo pipefail

# # Set up directory variables
# PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
# CERTIFICATE_ROOT="$PROJECT_ROOT/infra/certs"
# VAULT_TLS_DIR="$PROJECT_ROOT/infra/vault/tls"

# ROOT_CA_DIR="$PROJECT_ROOT/infra/certs/root"
# INTERMEDIATE_CA_DIR="$PROJECT_ROOT/infra/certs/intermediate"
# KEYSTORE_FILE="$PROJECT_ROOT/infra/certs/keystore.jks"
# CLIENT_CERT_KEYSTORE="$PROJECT_ROOT/infra/certs/client-cert.jks"

# # Clean up old directories and files if they exist
# rm -Rf ${ROOT_CA_DIR}
# rm -Rf ${INTERMEDIATE_CA_DIR}
# rm -Rf ${KEYSTORE_FILE}
# rm -Rf ${CLIENT_CERT_KEYSTORE}
# rm -Rf ${VAULT_TLS_DIR}

# # Check for openssl availability
# if [ ! -x "$(which openssl)" ] ; then
#    echo "[ERROR] No openssl in PATH"
#    exit 1
# fi

# # Check for keytool availability and set the path
# KEYTOOL=keytool
# if [  ! -x "${KEYTOOL}" ] ; then
#    KEYTOOL=${JAVA_HOME}/bin/keytool
# fi
# if [  ! -x "${KEYTOOL}" ] ; then
#    echo "[ERROR] No keytool in PATH/JAVA_HOME"
#    exit 1
# fi

# # Create necessary directories for the CA
# # mkdir -p ${CA_DIR}/private ${CA_DIR}/certs ${CA_DIR}/crl ${CA_DIR}/csr ${CA_DIR}/newcerts ${CA_DIR}/intermediate
# mkdir -p ${ROOT_CA_DIR}/{private,certs,newcerts} ${INTERMEDIATE_CA_DIR}/{private,certs,newcerts,crl,csr}
# mkdir -p "${VAULT_TLS_DIR}"

# echo "[INFO] Generating Root CA private key"
# openssl genrsa -aes256 -out ${ROOT_CA_DIR}/private/ca.key.pem 4096

# echo "[INFO] Generating Root CA certificate"
# openssl req -config ${CERTIFICATE_ROOT}/root_openssl.cnf \
#       -key ${ROOT_CA_DIR}/private/ca.key.pem \
#       -new -x509 -days 7300 -sha256 \
#       -extensions v3_ca -out ${ROOT_CA_DIR}/certs/ca.cert.pem

# # Initialize the CA database
# touch ${ROOT_CA_DIR}/index.txt ${ROOT_CA_DIR}/serial
# echo 1000 > ${ROOT_CA_DIR}/serial

# # Generate the Intermediate CA
# echo "[INFO] Generating Intermediate CA private key"
# openssl genrsa -aes256 -out ${INTERMEDIATE_CA_DIR}/private/intermediate.key.pem 4096

# echo "[INFO] Generating Intermediate CA CSR"
# openssl req -config ${CERTIFICATE_ROOT}/intermediate_openssl.cnf \
#       -new -sha256 \
#       -key ${INTERMEDIATE_CA_DIR}/private/intermediate.key.pem \
#       -out ${INTERMEDIATE_CA_DIR}/csr/intermediate.csr.pem

# echo "[INFO] Signing Intermediate CA CSR with Root CA"
# openssl ca -config ${CERTIFICATE_ROOT}/root_openssl.cnf \
#       -extensions v3_intermediate_ca \
#       -days 3650 -notext -md sha256 \
#       -in ${INTERMEDIATE_CA_DIR}/csr/intermediate.csr.pem \
#       -out ${INTERMEDIATE_CA_DIR}/certs/intermediate.cert.pem

# # Prepare Intermediate CA to issue certificates
# echo 1000 > ${INTERMEDIATE_CA_DIR}/serial
# touch ${INTERMEDIATE_CA_DIR}/index.txt

# # Import the CA chain into a Java keystore for applications to trust
# echo "[INFO] Importing CA chain into Java keystore"
# ${KEYTOOL} -importcert -keystore ${KEYSTORE_FILE} \
#            -alias rootCA -file ${ROOT_CA_DIR}/certs/ca.cert.pem \
#            -noprompt -storepass changeit

# ${KEYTOOL} -importcert -keystore ${KEYSTORE_FILE} \
#            -alias intermediateCA -file ${INTERMEDIATE_CA_DIR}/certs/intermediate.cert.pem \
#            -noprompt -storepass changeit

# echo "[INFO] Generating Vault server private key"
# openssl genrsa -out ${INTERMEDIATE_CA_DIR}/private/vault.key.pem 2048

# echo "[INFO] Generating CSR for Vault server"
# openssl req -config ${CERTIFICATE_ROOT}/intermediate_openssl.cnf \
#       -extensions v3_req \
#       -key ${INTERMEDIATE_CA_DIR}/private/vault.key.pem \
#       -new -sha256 -out ${INTERMEDIATE_CA_DIR}/csr/vault.csr.pem \
#       -subj "/C=US/ST=Wisconsin/L=Portage/O=twiceb/OU=Vault/CN=vault.twiceb.internal"

# echo "[INFO] Signing Vault server CSR"
# openssl ca -config ${CERTIFICATE_ROOT}/intermediate_openssl.cnf \
#       -extensions server_cert \
#       -days 375 -notext -md sha256 \
#       -in ${INTERMEDIATE_CA_DIR}/csr/vault.csr.pem \
#       -out ${INTERMEDIATE_CA_DIR}/certs/vault.cert.pem
# if [ $? -ne 0 ]; then
#     echo "[ERROR] Failed to sign Vault server CSR"
#     exit 1
# fi

# cp "${INTERMEDIATE_CA_DIR}/certs/vault.cert.pem" "${VAULT_TLS_DIR}/vault.cert.pem"
# cp "${INTERMEDIATE_CA_DIR}/private/vault.key.pem" "${VAULT_TLS_DIR}/vault.key.pem"

# cat "${INTERMEDIATE_CA_DIR}/certs/intermediate.cert.pem" "${ROOT_CA_DIR}/certs/ca.cert.pem" > "${VAULT_TLS_DIR}/ca_chain.pem"

# echo "[INFO] Setup completed successfully."