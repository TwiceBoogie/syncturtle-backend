#!/usr/bin/env bash

declare VAULT_ADDR
declare VAULT_TOKEN

generate_vault_var() {
  export VAULT_ADDR="http://127.0.0.1:8200"
  local vault_token
  vault_token=$(openssl rand -hex 32)
  export VAULT_TOKEN="$vault_token"
}

install_vault_via_brew() {
  echo "Installing vault using brew."
  brew tap hashicorp/tap
  brew install hashicorp/tap/vault

  local vault_version
  vault_version=$(vault --version 2>/dev/null)
  echo "Vault version: $vault_version"
}

install_vault_binary() {
  echo "Vault binary not found. Installing Vault..."
  local vault_dir=$1
  local vault_bin=$2

  mkdir -p "$vault_dir"
  cd "$vault_dir" || exit
  echo -e "\n"

  echo "Downloading Vault..."
  vault_version="1.15.4"
  # shellcheck disable=SC2154
  vault_filename="vault_${vault_version}_${os_name}_${arch}.zip"
  vault_url="https://releases.hashicorp.com/vault/${vault_version}/${vault_filename}"
  curl -LO "$vault_url"

  echo "Extracting Vault..."
  unzip "$vault_filename"
  rm "$vault_filename"

  chmod +x vault

  echo "Vault installed at: $vault_bin"
  echo "Vault version: $($vault_bin --version)"
}

check_vault_exist() {
  local vault_dir=$1
  local vault_bin=$2

  if command -v brew &>/dev/null ; then
    echo "Homebrew is installed."
    echo "Checking if hashicorp vault is installed."
    if vault_version=$(vault --version 2>/dev/null); then
        echo "Vault is installed."
        echo "Vault version: $vault_version"
    else
      install_vault_via_brew
    fi
    return 1
  elif [ -d "$vault_dir" ]; then
    if [ -x "$vault_bin" ]; then
      echo "Vault binary found at: $vault_bin"

      vault_version=$("$vault_bin" --version)
      echo "Vault version: $vault_version"
      return 1
    else
      return 0
    fi
  else
    return 0
  fi
}

vault_setup() {
  generate_vault_var
  local vault_dir
  vault_dir=$(pwd)/vault
  local vault_bin=$vault_dir/vault

  check_vault_exist "$vault_dir" "$vault_bin"
  local vault_exist=$?

  if [ "$vault_exist" -eq 0 ]; then
      install_vault_binary "$vault_dir" "$vault_bin"
  fi
}