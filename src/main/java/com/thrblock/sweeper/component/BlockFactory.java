package com.thrblock.sweeper.component;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.thrblock.sweeper.entity.Block;
import com.thrblock.sweeper.entity.SweeperTexture;

@Configuration
public class BlockFactory {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Block block(int i, int j, SweeperTexture stexture) {
        return new Block(i, j, stexture);
    }
}
