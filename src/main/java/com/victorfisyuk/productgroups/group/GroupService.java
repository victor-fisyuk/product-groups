package com.victorfisyuk.productgroups.group;

import com.victorfisyuk.productgroups.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class GroupService {
    private final GroupRepository groupRepository;

    @Retryable(ObjectOptimisticLockingFailureException.class)
    public void addProduct(long groupId, Product product) {
        Group group = groupRepository.findByIdAndOptimisticForceIncrement(groupId).orElseThrow();

        group.addProduct(product);
        log.info("{} - added product '{}' (count {})", group.getName(), product.getName(), group.getProducts().size());

        // Inherit tags from parent group to product
        inheritTags(group, product);
    }

    @Retryable(ObjectOptimisticLockingFailureException.class)
    public void assignTag(long groupId, String tag, @Nullable Consumer<Void> completionCallback) {
        log.info("<<< Started assigning tags");

        Group group = groupRepository.findByIdAndOptimisticForceIncrement(groupId).orElseThrow();

        group.getTags().add(tag);
        log.info("{} - assigned tag '{}': {}", group.getName(), tag, group.getTags());

        // Inherit group tags to group products
        group.getProducts().forEach(product -> inheritTags(group, product));

        if (completionCallback != null) {
            completionCallback.accept(null);
        }

        log.info(">>> Finished assigning tags");
    }

    private void inheritTags(Group group, Product product) {
        product.setTags(new HashSet<>(group.getTags()));
        log.info("{} - inherited tags to product '{}': {}", group.getName(), product.getName(), group.getTags());
    }
}
