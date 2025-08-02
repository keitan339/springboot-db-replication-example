FROM ubuntu:22.04

ARG JAVA_VERSION
ARG MAVEN_VERSION
ARG GRADLE_VERSION
ARG NODE_VERSION

ARG USER_NAME=developer
ARG USER_UID=1000
ARG USER_GID=1000

# インストールの共通設定
ENV DEBIAN_FRONTEND=noninteractive

# 日本語環境
ENV LANG=ja_JP.UTF-8 \
    LANGUAGE=ja_JP.UTF-8 \
    LC_ALL=ja_JP.UTF-8 \
    TZ=Asia/Tokyo

# 利用シェルをbashに変更
RUN echo "dash dash/sh boolean false" | debconf-set-selections \
    && dpkg-reconfigure dash || true

# パッケージの導入
RUN apt-get update \
    # 日本語環境化
    && apt-get install -y --no-install-recommends \
    tzdata  \
    locales \
    && locale-gen ja_JP.UTF-8 \
    # 基本パッケージ
    && apt-get install -y --no-install-recommends \
    sudo \
    man-db \
    procps \
    fzf \
    ssh \
    iproute2 \
    git \
    gh \
    jq \
    vim \
    zip \
    unzip \
    curl \
    wget \
    ca-certificates \
    gnupg \
    software-properties-common \
    build-essential \
    # tmux
    && apt-get install -y --no-install-recommends \
    xsel \
    tmux \
    # 不要なパッケージの削除とキャッシュのクリア
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

# 作業ユーザーの作成
RUN if [ -z "${USER_NAME}" ] || [ -z "${USER_UID}" ] || [ -z "${USER_GID}" ]; then \
        echo "Error: USER_NAME and USER_UID and USER_GID must be set" && exit 1; \
    fi \
    && groupadd --gid ${USER_GID} ${USER_NAME} \
    && useradd --uid ${USER_UID} --gid ${USER_GID} -m ${USER_NAME} --shell /bin/bash \
    && passwd -d ${USER_NAME} \
    && echo ${USER_NAME} ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/${USER_NAME} \
    && chmod 0440 /etc/sudoers.d/${USER_NAME}

# NodeJS
RUN curl -fsSLO --compressed "https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-x64.tar.xz" \
    && tar -xJf "node-v${NODE_VERSION}-linux-x64.tar.xz" -C /usr/local --strip-components=1 --no-same-owner \
    && rm node-v${NODE_VERSION}-linux-x64.tar.xz \
    && ln -s /usr/local/bin/node /usr/local/bin/nodejs

# Java
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    openjdk-${JAVA_VERSION}-jdk \
    # 不要なパッケージの削除とキャッシュのクリア
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /opt/maven && cd /opt/maven \
    && wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar zxf apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && ln -s /opt/maven/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME=/opt/maven/apache-maven-${MAVEN_VERSION}
RUN mkdir -p /opt/gradle && cd /opt/gradle \
    && wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip \
    && ln -s /opt/gradle/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle
ENV GRADLE_HOME=/opt/gradle/gradle-${GRADLE_VERSION}

# Docker（docker in docker対応）
RUN install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc \
    && chmod a+r /etc/apt/keyrings/docker.asc \
    && echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
      $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
      sudo tee /etc/apt/sources.list.d/docker.list > /dev/null \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    docker-buildx-plugin \
    docker-compose-plugin \
    # 不要なパッケージの削除とキャッシュのクリア (イメージサイズ削減のため)
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/* \
    # 実行ユーザでdockerを実行可能にする。
    && usermod -aG docker ${USER_NAME}

# ssh-server
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    openssh-server \
    && mkdir /var/run/sshd \
    && sed -ri 's/^#?PermitEmptyPasswords\s+.*/PermitEmptyPasswords yes/' /etc/ssh/sshd_config \
    && sed -ri 's/^#?UsePAM\s+.*/UsePAM no/' /etc/ssh/sshd_config \
    && sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd \
    # 不要なパッケージの削除とキャッシュのクリア (イメージサイズ削減のため)
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*
EXPOSE 22

# claude-code
RUN npm install -g @anthropic-ai/claude-code
ENV CLAUDE_CONFIG_DIR=/home/${USER_NAME}/.claude

# PostgreSQLクライアント
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    postgresql-common \
    && /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh -y \
    && apt-get install -y --no-install-recommends \
    postgresql-client \
    # 不要なパッケージの削除とキャッシュのクリア (イメージサイズ削減のため)
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

# ユーザ切り替え
USER ${USER_NAME}
WORKDIR /workspace

# プロンプト設定
RUN echo "# ターミナル設定" >> /home/${USER_NAME}/.bashrc \
    && echo "export PS1='${debian_chroot:+($debian_chroot)}\[\033[01;34m\]\w\[\033[00m\]\$ '" >> /home/${USER_NAME}/.bashrc \
    && echo "" >> /home/${USER_NAME}/.bashrc

# npm-global-pathの変更
RUN mkdir ~/.npm-global \
    && npm config set prefix '~/.npm-global' \
    && echo "# npm global installのパス設定" >> /home/${USER_NAME}/.bashrc \
    && echo "export PATH=~/.npm-global/bin:$PATH" >> /home/${USER_NAME}/.bashrc \
    && echo "" >> /home/${USER_NAME}/.bashrc

# claude-codeのインストール（自動アップデートがログユーザで行われるため、rootユーザでイントールしない）
RUN npm install -g @anthropic-ai/claude-code \
    && mkdir ~/.claude \
    && echo "# claude codeの設定" >> /home/${USER_NAME}/.bashrc \
    && echo "alias yolo='claude --dangerously-skip-permissions'" >> /home/${USER_NAME}/.bashrc \
    && echo "" >> /home/${USER_NAME}/.bashrc
ENV CLAUDE_CONFIG_DIR=/home/${USER_NAME}/.claude

# tmux設定
RUN echo "# tmux設定" >> /home/${USER_NAME}/.bashrc \
    && echo "alias tmux-a='tmux new-session -A -s \"${PWD/$HOME/\\~}\"'" >> /home/${USER_NAME}/.bashrc \
    && echo "" >> /home/${USER_NAME}/.bashrc
COPY --chown=${USER_UID}:${USER_GID} ./tmux/tmux.conf /home/${USER_NAME}/.config/tmux/tmux.conf

# コンテナ起動スクリプトを設定
COPY --chown=${USER_UID}:${USER_GID} ./entrypoint.sh /entrypoint.sh
RUN chmod 755 /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
