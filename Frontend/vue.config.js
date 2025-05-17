const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 5173, // Boys I added this so the app runs on port 5173, if you look carefully at the number 5173, it is the word "site" splled in numerals 
  }
});
