yaml
# Same as above, but change these lines:
      - name: Build project
        run: npm run build

      - name: Deploy to GH Pages
        uses: peaceiris/actions-gh-pages@v3
        with:
          publish_dir: ./Frontend/dist