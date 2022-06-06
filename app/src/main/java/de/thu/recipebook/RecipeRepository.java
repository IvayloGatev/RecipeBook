package de.thu.recipebook;

import java.util.ArrayList;

public class RecipeRepository {
    private ArrayList<Recipe> recipes;

    public RecipeRepository() {
        recipes = new ArrayList<>();

        recipes.add(new Recipe("Classic Lasagna", "Italy",
                "3/4 lb. lasagna noodles \n1 tsp. extra-virgin olive oil, plus more for drizzling \n2 lb. ground beef \n4 cloves garlic, minced \n2 tsp. dried oregano \nKosher salt \nFreshly ground black pepper \n2 (32-0z.) jars marinara \n16 oz. whole milk ricotta \n1/2 c. freshly grated Parmesan, divided \n1/4 c. chopped parsley, plus more for garnish \n1 large egg \n2 lb. sliced mozzarella",
                "Preheat oven to 375º. In a large pot of salted boiling water, cook pasta according to package directions until al dente, less 2 minutes. Drain and drizzle a bit of olive oil to prevent noodles from sticking together. Meanwhile, in a large pot over medium-high heat, heat oil. Cook ground beef until no longer pink, breaking up with a wooden spoon. Remove from heat and drain fat. Return beef to skillet and add garlic and oregano and cook, stirring, for 1 minute. Season with salt and pepper, then add marinara and stir until warmed through.  Combine ricotta, 1/4 cup Parmesan, parsley, and egg in a large mixing bowl and season with salt and pepper. Set aside. In a large casserole dish, evenly spread a quarter of the meat sauce across the bottom of the dish, then top with a single layer of lasagna noodles, a layer of ricotta mixture, a single layer of mozzarella, and a layer of meat sauce. Repeat layers, topping the last layer of noodles with meat sauce, Parmesan, and mozzarella. Cover with foil and bake for 15 minutes, then increase temperature to 400º and bake uncovered for 18 to 20 minutes. Garnish with parsley before serving."
        ));
        recipes.add(new Recipe("Weisswurst sausage", "Germany",
                "1 tablespoon lard or vegetable oil \n1/2 cup minced white onion \n3 pounds white meat, pork, veal, turkey, rabbit, chicken \n1 pound bacon ends or fatty pork shoulder \n20 grams salt, about 2 tablespoons plus a teaspoon \n1 tablespoon minced parsley \n1/2 teaspoon dry mustard powder \n1/2 teaspoon powdered ginger \n1/2 teaspoon white pepper \n1/2 teaspoon mace \n1/2 teaspoon ground cardamom \nGrated zest of a lemon \n1 teaspoon C-Bond carrot fiber (optional) \n1 cup ice water \nHog casings",
                "Heat the lard in a small pan and cook the onions until soft. Do not brown them. Let them cool to room temperature, or refrigerate them. This can be done up to a day in advance. Soak about 10 feet of hog casings in warm water. When you are ready to grind, mix the meats, salt, parsley, spices and lemon zest. Grind through a coarse or medium die. Put the mixture in the freezer while you clean up, or, if the meat is still below 40°F, grind again through a fine die, at least 4.5 mm and ideally 3 mm. This time, definitely put the meat in the freezer while you clean up. Once the meat is at about 34°F, put it in a large bowl with the water and C-Bind, if you are using it. Mix this with your clean hands for about 90 seconds, or until the mixture binds together as a cohesive mass that you can pick up in one glob. Your hands should hurt from the cold. Put a length of casing on your sausage stuffer and fill it with the weisswurst. Crank out one large length of sausage, leaving about 3 to 5 inches of \"tail,\" unfilled casing, on either end. You don't want to fill the casings overly tight just yet. Repeat this process until you have all the sausage in casings. Get a large pot of water hot, about 160°F. To form links, pinch off a link of about 6 inches long at one end of the length. Spin it away from you to set the link. Now move down the length and pinch off another link, but this time spin it towards you. Keep doing this, spinning in alternate directions, until you get to the end of the length. Doing this helps prevents the links from coming apart. Tie off the ends. Now, to tighten them, get a clean needle or sausage pricker. Gently compress the meat in each length, spinning it a little more in the direction you first spun. You will see air pockets. Prick the casing to remove them, again gently compressing the links to fill the casing. Do this for every link. Carefully lower the weisswurst into the hot water. They will want to unspin a bit so watch for that. Poach them gently for 20 minutes or so. While they are cooking, fill a large basin with ice water. Dunk the links in this ice water after they've cooked. Leave them there for 10 minutes. Pat them dry and you are ready to go. They will keep for about 5 days in the fridge, and can be frozen"
        ));
    }

    public Recipe getNextRecipe(Recipe currentRecipe) {
        int nextIndex = recipes.indexOf(currentRecipe) + 1;
        if (nextIndex >= recipes.size()) {
            nextIndex = 0;
        }
        return recipes.get(nextIndex);
    }
}
