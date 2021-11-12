package mindustry.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.Interp.*;
import arc.util.*;
import mindustry.world.*;
import mindustry.world.blocks.production.GenericCrafter.*;

//TODO
public class DrawCrucible extends DrawBlock{
    public TextureRegion top, bottom;
    public Color flameColor = Color.valueOf("f58349"), midColor = Color.valueOf("f2d585");
    public float flameRad = 1f, circleSpace = 2f, flameRadiusScl = 10f, flameRadiusMag = 0.6f, circleStroke = 1.5f;

    public float alpha = 0.5f;
    public int particles = 30;
    public float particleLife = 70f, particleRad = 7f, particleSize = 3f, fadeMargin = 0.4f;
    public Interp particleInterp = new PowIn(1.5f);

    @Override
    public void draw(GenericCrafterBuild build){
        Draw.rect(bottom, build.x, build.y);

        if(build.warmup > 0f && flameColor.a > 0.001f){
            Lines.stroke(circleStroke * build.warmup);

            float si = Mathf.absin(flameRadiusScl, flameRadiusMag);
            float a = alpha * build.warmup;
            Draw.blend(Blending.additive);

            Draw.color(midColor, a);
            Fill.circle(build.x, build.y, flameRad + si);

            Draw.color(flameColor, a);
            Lines.circle(build.x, build.y, (flameRad + circleSpace + si) * build.warmup);

            float base = (Time.time / particleLife);
            rand.setSeed(build.id);
            for(int i = 0; i < particles; i++){
                float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
                float angle = rand.random(360f);
                float len = particleRad * particleInterp.apply(fout);
                Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)));
                Fill.circle(
                    build.x + Angles.trnsx(angle, len),
                    build.y + Angles.trnsy(angle, len),
                    particleSize * fin * build.warmup
                );
            }

            Draw.blend();
            Draw.reset();
        }

        Draw.rect(build.block.region, build.x, build.y);
        if(top.found()) Draw.rect(top, build.x, build.y);
    }

    @Override
    public void load(Block block){
        top = Core.atlas.find(block.name + "-top");
        bottom = Core.atlas.find(block.name + "-bottom");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{bottom, block.region, top};
    }
}
