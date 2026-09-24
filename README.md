# Shadow Servants — Forge 1.21.1

Mod completo para Minecraft 1.21.1 usando Forge 52.1.0.

## Sistema
- Cada 10 criaturas derrotadas pelo jogador libera 1 servo.
- O progresso fica salvo nos dados persistentes do jogador.
- `G` abre o menu.
- Servos seguem o dono e atacam a última criatura que o jogador acertou.
- Se um servo morrer, o vínculo disponível é consumido.
- Guardar um servo não consome o vínculo.

## Comandos
`/shadowservants status`
`/shadowservants summon minecraft:zombie`
`/shadowservants dismiss minecraft:zombie`
`/shadowservants dismiss_all`

## Ambiente
- Minecraft 1.21.1
- Forge 52.1.0
- Java 21

## Compilar
No Windows:
`gradlew.bat build`

No Linux:
`./gradlew build`

O JAR sai em `build/libs/`.
