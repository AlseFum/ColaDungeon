# Cheat
**author** chokecola
### BasicJianRong
- p 23
- load_template
- cheatpotion
```ItemSprite_constructor

	
	public ItemSprite(String assetPath, int frame) {
		super(assetPath);
		frame(frame);
	}
```
```Assets_Sprite
		public static final String BREAD = "minecraft/bread.png";
```

```ItemSprite_import
import food.items.com.coladungeon.SupplyRation;
```
```ItemSprite_link
		if ( heap.peek() instanceof SupplyRation ) {
			this.heap = heap;
			this.texture = TextureCache.get( Assets.Sprites.BREAD );
			frame(0);
			renderShadow = true;
			visible = heap.seen;
			place(heap.pos);
			return;
		}
```ItemSprite_link
- asset asset/bread.png ./minecraft/bread.png
### WarriorMuchHealing =>env1
``` HeroClass_initHero
        new PotionOfHealing().quantity(16).collect();
        new PotionOfStrength().quantity(16).collect();
        //console.log("ohh man")
```
``` HeroClass_initWarrior

        new ScrollOfUpgrade().quantity(1145).collect();

        
```
``` HeroClass_initHero
        
        //console.log("ohh man")
        //utf
```
``` then
//#+ condi1
//#- condi1
```
### then env1=>env2
- godiff
```condi1
okay!
```condi1


