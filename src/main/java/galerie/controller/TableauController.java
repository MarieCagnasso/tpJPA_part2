package galerie.controller;

import galerie.dao.ArtisteRepository;
import galerie.dao.TableauRepository;
import galerie.entity.Tableau;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/tableaux")
public class TableauController {

    @Autowired
    private TableauRepository dao;

    @Autowired
    private ArtisteRepository artisteRepository;

    /**
     * Affiche toutes les catégories dans la base
     *
     * @param model pour transmettre les informations à la vue
     * @return le nom de la vue à afficher ('afficheTableaux.html')
     */
    @GetMapping(path = "show")
    public String afficheTousLesTableaux (Model model) {
        model.addAttribute("tableaux", dao.findAll());
        return "afficheTableaux";
    }

    /**
     * Appelé par le lien 'Supprimer' dans 'afficheGaleries.html'
     *
     * @param tableau à partir de l'id de la galerie transmis en paramètre, Spring fera une requête SQL SELECT pour
     * chercher le tableau dans la base
     * @param redirectInfo pour transmettre des paramètres lors de la redirection
     * @return une redirection vers l'affichage de la liste des galeries
     */
    @GetMapping(path = "delete")
    public String supprimeUnTableauePuisMontreLaListe(@RequestParam("id") Tableau tableau, RedirectAttributes redirectInfo) {
        String message = "Le tableau '" + tableau.getTitre() + "' a bien été supprimée";

        try {
            dao.delete(tableau);
        } catch (DataIntegrityViolationException e) {
            message = "Erreur : Impossible de supprimer le tableau '" + tableau.getTitre() + "', il faut d'abord le supprimer de ses expositions";
        }
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show";
    }

    @GetMapping(path = "add")
    public String montreLeFormulairePourAjout(@ModelAttribute("tableau") Tableau tableau, Model model) {
        model.addAttribute("artistes", artisteRepository.findAll());
        return "formulaireTableau";
    }

    /**
     * Appelé par 'formulaireGalerie.html', méthode POST
     *
     * @param tableau Un tableau initialisée avec les valeurs saisies dans le formulaire
     * @param redirectInfo pour transmettre des paramètres lors de la redirection
     * @return une redirection vers l'affichage de la liste des tableaux
     */
    @PostMapping(path = "save")
    public String ajouteLeTableauPuisMontreLaListe(Tableau tableau, RedirectAttributes redirectInfo) {
        String message;
        try {
            dao.save(tableau);
            message = "Le tableau '" + tableau.getTitre() + "' a été correctement enregistrée";
        } catch (DataIntegrityViolationException e) {
            message = "Erreur : Le tableau '" + tableau.getTitre() + "' existe déjà";
        }
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show";
    }

}
