## How it works
This plugin fixes the exploit allowing players to keep their mace damage bonus by abusing ghost blocks.

The moment that a player tries to damage anything with a mace with this exploit, it will be entirely cancelled with no damage dealt. Other than in v1.0, this will not affect regular mace use at all, if there are any false positives then make a [new issue here](https://github.com/DeactivatedMan/mace-glitch-fix/issues).

## Showcase GIF
![Showcase of what happens in survival. The attack gets cancelled and the player dies to fall damage.](https://cdn.modrinth.com/data/4EbMD1LY/images/dd6c40d33a41c38b327ed4b7cd13d89becfe805a.gif)

## Config
By default, any player trying to abuse this exploit will take the fall damage they would have previously after the attack gets cancelled, you can disable this by changing `cancel-fall-dmg` to true. If you want each attempt to be logged, there is also a config (this being `log-cancels`).

## Testing
For testing purposes, I have left in a `developer-mode` toggle, which will make the creation of ghost blocks significantly easier. All you have to do is place a piece of bedrock, and two blocks under it will become a ghost block provided that it's a solid block.