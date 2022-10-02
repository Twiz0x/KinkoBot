package fr.twizox.kinkobot.listeners;

import fr.twizox.kinkobot.Roles;
import fr.twizox.kinkobot.utils.NiceColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class ButtonClickListener extends ListenerAdapter {

    private final List<Roles> roles = List.of(Roles.PIRATE, Roles.MARINE, Roles.REBELLE);

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        Member member = event.getMember();
        Guild guild = event.getGuild();
        if (member == null || guild == null) return;

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(NiceColors.GREEN.getColor())
                .setFooter("KinkoMC - 2022", event.getJDA().getSelfUser().getAvatarUrl());

        boolean citizen = false;
        for (Roles roleEnum : roles) {
            Role role = roleEnum.getRole(guild);
            if (!member.getRoles().contains(role)) continue;
            guild.removeRoleFromMember(member, role).queue();
            if (roleEnum == getRoleEnum(event)) citizen = true;
        }
        if (citizen) {
            event.replyEmbeds(embedBuilder.setDescription("Vous êtes de nouveau Citoyen !").build()).setEphemeral(true).queue();
            return;
        }

        Role roleToAdd = getRole(event);
        guild.addRoleToMember(member, roleToAdd).submit().thenRun(() -> {
            event.replyEmbeds(embedBuilder
                    .setDescription("Vous avez rejoint les " + getRole(event).getName() + "s !")
                    .build()).setEphemeral(true).queue();
        });

    }

    private Roles getRoleEnum(ButtonInteractionEvent event) {
        return Roles.valueOf(event.getComponentId().toUpperCase());
    }

    private Role getRole(ButtonInteractionEvent event) {
        return getRoleEnum(event).getRole(event.getGuild());
    }

}
