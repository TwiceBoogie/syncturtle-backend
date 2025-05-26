#!/usr/bin/env bash

# Set up directory variables
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_CA_DIR=work/ca/root
INTERMEDIATE_CA_DIR=work/ca/intermediate
KEYSTORE_FILE=work/keystore.jks
CLIENT_CERT_KEYSTORE=work/client-cert.jks

# Clean up old directories and files if they exist
if [[ -d work/ca/root ]] ; then
    rm -Rf ${ROOT_CA_DIR}
fi

if [[ -d work/ca/intermediate ]] ; then
      rm -RF ${INTERMEDIATE_CA_DIR}
fi

if [[ -f ${KEYSTORE_FILE} ]] ; then
    rm -Rf ${KEYSTORE_FILE}
fi

if [[ -f ${CLIENT_CERT_KEYSTORE} ]] ; then
    rm -Rf ${CLIENT_CERT_KEYSTORE}
fi

# Check for openssl availability
if [ ! -x "$(which openssl)" ] ; then
   echo "[ERROR] No openssl in PATH"
   exit 1
fi

# Check for keytool availability and set the path
KEYTOOL=keytool
if [  ! -x "${KEYTOOL}" ] ; then
   KEYTOOL=${JAVA_HOME}/bin/keytool
fi
if [  ! -x "${KEYTOOL}" ] ; then
   echo "[ERROR] No keytool in PATH/JAVA_HOME"
   exit 1
fi

# Create necessary directories for the CA
# mkdir -p ${CA_DIR}/private ${CA_DIR}/certs ${CA_DIR}/crl ${CA_DIR}/csr ${CA_DIR}/newcerts ${CA_DIR}/intermediate
mkdir -p ${ROOT_CA_DIR}/{private,certs,newcerts} ${INTERMEDIATE_CA_DIR}/{private,certs,newcerts,crl,csr}

echo "[INFO] Generating Root CA private key"
openssl genrsa -aes256 -out ${ROOT_CA_DIR}/private/ca.key.pem 4096

echo "[INFO] Generating Root CA certificate"
openssl req -config ${DIR}/root_openssl.cnf \
      -key ${ROOT_CA_DIR}/private/ca.key.pem \
      -new -x509 -days 7300 -sha256 \
      -extensions v3_ca -out ${ROOT_CA_DIR}/certs/ca.cert.pem

# Initialize the CA database
touch ${ROOT_CA_DIR}/index.txt ${ROOT_CA_DIR}/serial
echo 1000 > ${ROOT_CA_DIR}/serial

# Generate the Intermediate CA
echo "[INFO] Generating Intermediate CA private key"
openssl genrsa -aes256 -out ${INTERMEDIATE_CA_DIR}/private/intermediate.key.pem 4096

echo "[INFO] Generating Intermediate CA CSR"
openssl req -config ${DIR}/intermediate_openssl.cnf \
      -new -sha256 \
      -key ${INTERMEDIATE_CA_DIR}/private/intermediate.key.pem \
      -out ${INTERMEDIATE_CA_DIR}/csr/intermediate.csr.pem

echo "[INFO] Signing Intermediate CA CSR with Root CA"
openssl ca -config ${DIR}/root_openssl.cnf \
      -extensions v3_intermediate_ca \
      -days 3650 -notext -md sha256 \
      -in ${INTERMEDIATE_CA_DIR}/csr/intermediate.csr.pem \
      -out ${INTERMEDIATE_CA_DIR}/certs/intermediate.cert.pem

# Prepare Intermediate CA to issue certificates
echo 1000 > ${INTERMEDIATE_CA_DIR}/serial
touch ${INTERMEDIATE_CA_DIR}/index.txt

# Import the CA chain into a Java keystore for applications to trust
echo "[INFO] Importing CA chain into Java keystore"
${KEYTOOL} -importcert -keystore ${KEYSTORE_FILE} \
           -alias rootCA -file ${ROOT_CA_DIR}/certs/ca.cert.pem \
           -noprompt -storepass changeit

${KEYTOOL} -importcert -keystore ${KEYSTORE_FILE} \
           -alias intermediateCA -file ${INTERMEDIATE_CA_DIR}/certs/intermediate.cert.pem \
           -noprompt -storepass changeit

echo "[INFO] Generating Vault server private key"
openssl genrsa -out ${INTERMEDIATE_CA_DIR}/private/vault.key.pem 2048

echo "[INFO] Generating CSR for Vault server"
openssl req -config ${DIR}/intermediate_openssl.cnf \
      -extensions v3_req \
      -key ${INTERMEDIATE_CA_DIR}/private/vault.key.pem \
      -new -sha256 -out ${INTERMEDIATE_CA_DIR}/csr/vault.csr.pem \
      -subj "/C=US/ST=Wisconsin/L=Portage/O=twiceb/OU=Vault/CN=vault.twiceb.internal"

echo "[INFO] Signing Vault server CSR"
openssl ca -config ${DIR}/intermediate_openssl.cnf \
      -extensions server_cert \
      -days 375 -notext -md sha256 \
      -in ${INTERMEDIATE_CA_DIR}/csr/vault.csr.pem \
      -out ${INTERMEDIATE_CA_DIR}/certs/vault.cert.pem
if [ $? -ne 0 ]; then
    echo "[ERROR] Failed to sign Vault server CSR"
    exit 1
fi

echo "[INFO] Setup completed successfully."