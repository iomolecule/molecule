/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iomolecule.util.ds;

import java.util.Optional;

public interface TreeNode<T> {

    public String getName();

    public <T> T getData();

    public void setData(T data);

    public boolean hasChildren();

    public TreeNode<T>[] getChildren();

    public boolean isValidChild(String path);

    public TreeNode<T> getChildAtPath(String path) throws InvalidTreeNodePathException;

    public <T> T getDataAtPath(String path) throws InvalidTreeNodePathException;

    public void setDataAtPath(String path,T data) throws InvalidTreeNodePathException;

    public Optional<TreeNode<T>> getChild(String name);
}
